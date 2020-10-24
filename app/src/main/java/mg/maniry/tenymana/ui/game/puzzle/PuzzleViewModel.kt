package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.*
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.ui.game.colors.DefaultColor
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.GameViewModel

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

    val displayVerse = Transformations.map(gameViewModel.puzzle) {
        "${it?.verse?.book} ${it?.verse?.chapter}:${it?.verse?.verse}"
    }

    private val _route = MutableLiveData<Route?>()
    val route: LiveData<Route?> = _route

    private val scoreSyncObserver = Observer<Int> {
        _score.postValue(it + gameViewModel.session.value!!.progress.totalScore)
    }

    private val puzzleStateObserver = Observer<Puzzle?> {
        if (it != null) {
            initScreen()
            observeScore()
        }
    }

    private fun observeScore() {
        gameViewModel.puzzle.value!!.score.observeForever(scoreSyncObserver)
    }

    private fun initScreen() {
        // go to the correct route
    }

    init {
        gameViewModel.puzzle.observeForever(puzzleStateObserver)
    }

    override fun onCleared() {
        super.onCleared()
        gameViewModel.puzzle.removeObserver(puzzleStateObserver)
        gameViewModel.puzzle.value?.score?.removeObserver(scoreSyncObserver)
    }
}
