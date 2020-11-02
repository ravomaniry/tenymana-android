package mg.maniry.tenymana.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.list.GamesListFragment
import mg.maniry.tenymana.ui.game.paths.PathsFragment
import mg.maniry.tenymana.ui.game.puzzle.PuzzleFragment
import mg.maniry.tenymana.ui.game.solution.SolutionFragment
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.mountChild
import org.koin.android.ext.android.inject

class GameFragment : Fragment() {
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        observeRoute()
        return inflater.inflate(R.layout.game_screen, container, false)
    }

    private fun initViewModel() {
        val viewModels: SharedViewModels by inject()
        val userRepo: UserRepo by inject()
        val gameRepo: GameRepo by inject()
        val puzzleBuilder: PuzzleBuilder by inject()
        val bibleRepo: BibleRepo by inject()
        val dispatchers: KDispatchers by inject()
        val fct = GameViewModelFactory(
            viewModels.app,
            userRepo,
            gameRepo,
            bibleRepo,
            puzzleBuilder,
            dispatchers
        )
        viewModel = ViewModelProvider(this, fct).get(GameViewModel::class.java)
    }

    private fun observeRoute() {
        viewModel.screen.observe(viewLifecycleOwner, Observer {
            val child = when (it) {
                Screen.GAMES_LIST -> GamesListFragment(viewModel)
                Screen.PATHS_LIST -> PathsFragment(viewModel)
                Screen.PUZZLE -> PuzzleFragment(viewModel)
                Screen.PUZZLE_SOLUTION -> SolutionFragment(viewModel)
                else -> null
            }
            if (child != null) {
                mountChild(child, R.id.gameScreenBody)
            }
        })
    }
}
