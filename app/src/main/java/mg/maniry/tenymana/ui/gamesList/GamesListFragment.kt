package mg.maniry.tenymana.ui.gamesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GamesListScreenBinding
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import org.koin.android.ext.android.inject

class GamesListFragment : Fragment() {
    private val userRepo: UserRepo by inject()
    private val gameRepo: GameRepo by inject()
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GamesListScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        initRecyclerView()
        observeNav()
        return binding.root
    }

    private fun initViewModel() {
        // same instance across the app (appVM and gamesVM)
        val appVm = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        val fct = GameViewModelFactory(appVm, userRepo, gameRepo)
        viewModel = ViewModelProvider(requireActivity(), fct).get(GameViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.games_list_screen, container, false)
        binding.lifecycleOwner = this
    }

    private fun initRecyclerView() {
        val adapter = SessionAdapter(viewModel.onSessionClick)
        binding.gamesList.adapter = adapter
        viewModel.sessions.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
            }
        })
    }

    private fun observeNav() {
        viewModel.screen.observe(viewLifecycleOwner, Observer {
            val direction = when (it) {
                Screen.PUZZLE -> GamesListFragmentDirections.listToPuzzle()
                else -> null
            }
            navigate(direction)
        })
    }

    private fun navigate(direction: NavDirections?) {
        if (viewModel.shouldNavigate) {
            viewModel.shouldNavigate = false
            if (direction != null) {
                findNavController().navigate(direction)
            }
        }
    }
}
