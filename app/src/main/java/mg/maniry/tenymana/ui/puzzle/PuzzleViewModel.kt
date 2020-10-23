package mg.maniry.tenymana.ui.puzzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.colors.DefaultColor
import mg.maniry.tenymana.ui.colors.GameColors
import mg.maniry.tenymana.ui.gamesList.GameViewModel

enum class Route {
    BONUS,
    PUZZLE,
    COMPLETE
}

class PuzzleViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    private val _color = MutableLiveData<GameColors>(DefaultColor)
    val colors: LiveData<GameColors> = _color

    private val _score = MutableLiveData<Int>()
    val score: LiveData<String> = Transformations.map(_score) { it.toString() }

    val puzzle: LiveData<Puzzle?> = gameViewModel.puzzle

    val displayVerse = Transformations.map(puzzle) {
        "${it?.verse?.book} ${it?.verse?.chapter}:${it?.verse?.verse}"
    }

    private val _route = MutableLiveData<Route?>()
    val route: LiveData<Route?> = _route
}
