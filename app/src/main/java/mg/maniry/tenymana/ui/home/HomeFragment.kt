package mg.maniry.tenymana.ui.home

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
import mg.maniry.tenymana.databinding.HomeScreenBinding
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeScreenBinding

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
        val viewModels: SharedViewModels by inject()
        val fact = HomeViewModelFactory(viewModels.app)
        viewModel = ViewModelProvider(this, fact).get(HomeViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeNav() {
        viewModel.screen.observe(viewLifecycleOwner, Observer {
            val d = when (it) {
                Screen.GAMES_LIST,
                Screen.PATHS_LIST,
                Screen.PUZZLE,
                Screen.PUZZLE_SOLUTION -> HomeFragmentDirections.homeToGames()
                else -> null
            }
            navigate(d)
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
