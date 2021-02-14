package mg.maniry.tenymana.ui.game.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GamesListScreenBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.GameViewModel
import org.koin.android.ext.android.inject

class GamesListFragment : Fragment() {
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
        return binding.root
    }

    private fun initViewModel() {
        val viewModels: SharedViewModels by inject()
        viewModel = viewModels.game
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.games_list_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initRecyclerView() {
        val adapter = SessionAdapter(viewModel.onSessionClick, viewModel::onDeleteJourney)
        binding.gamesList.adapter = adapter
        viewModel.sessions.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
            }
        })
    }
}
