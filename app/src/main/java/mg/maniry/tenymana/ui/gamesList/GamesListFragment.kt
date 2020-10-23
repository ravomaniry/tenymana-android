package mg.maniry.tenymana.ui.gamesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GamesListScreenBinding
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.utils.observeGamesNav
import org.koin.android.ext.android.inject

class GamesListFragment : Fragment() {
    private val viewModels: SharedViewModels by inject()
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
        viewModel = viewModels.game
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
        observeGamesNav(viewModel) {
            when (it) {
                Screen.PATHS_LIST -> GamesListFragmentDirections.gamesListToPaths()
                else -> null
            }
        }
    }
}
