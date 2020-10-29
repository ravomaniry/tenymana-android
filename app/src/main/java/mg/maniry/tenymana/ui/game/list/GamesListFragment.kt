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
import mg.maniry.tenymana.ui.game.GameViewModel

class GamesListFragment(
    private val gameViewModel: GameViewModel
) : Fragment() {
    private lateinit var binding: GamesListScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initRecyclerView()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.games_list_screen, container, false)
        binding.lifecycleOwner = this
    }

    private fun initRecyclerView() {
        val adapter = SessionAdapter(gameViewModel.onSessionClick)
        binding.gamesList.adapter = adapter
        gameViewModel.sessions.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
            }
        })
    }
}
