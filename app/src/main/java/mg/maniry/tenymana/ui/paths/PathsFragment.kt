package mg.maniry.tenymana.ui.paths

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenBinding
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.gamesList.GameViewModel
import mg.maniry.tenymana.ui.gamesList.GameViewModelFactory
import mg.maniry.tenymana.ui.utils.observeGamesNav
import org.koin.android.ext.android.inject

class PathsFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: PathsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        observeNav()
        return binding.root
    }

    private fun initViewModel() {
        // singletons
        val userRepo: UserRepo by inject()
        val gameRepo: GameRepo by inject()
        val appVM = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        val vmFct = GameViewModelFactory(appVM, userRepo, gameRepo)
        viewModel = ViewModelProvider(requireActivity(), vmFct).get(GameViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.paths_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeNav() {
        observeGamesNav(viewModel) {
            when (it) {
                Screen.PUZZLE -> PathsFragmentDirections.pathsListToPuzzle()
                else -> null
            }
        }
    }
}
