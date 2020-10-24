package mg.maniry.tenymana.ui.game.paths

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenBinding
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.utils.observeGamesNav
import org.koin.android.ext.android.inject

class PathsFragment : Fragment() {
    private val sharedViewModels: SharedViewModels by inject()
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
        viewModel = sharedViewModels.game
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
