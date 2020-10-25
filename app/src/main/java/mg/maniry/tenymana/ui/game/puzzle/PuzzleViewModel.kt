package mg.maniry.tenymana.ui.game.puzzle

import androidx.lifecycle.*
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.ui.game.colors.DefaultColors
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.game.colors.LinkClearColors

enum class Route {
    LOADER,
    LINK_CLEAR
}

class PuzzleViewModel(
    private val gameViewModel: GameViewModel
) : ViewModel() {
    val colors: LiveData<GameColors> = Transformations.map(gameViewModel.puzzle) {
        when (it) {
            is LinkClearPuzzle -> LinkClearColors()
            else -> DefaultColors()
        }
    }

    private val _score = MutableLiveData<Int>()
    val score: LiveData<String> = Transformations.map(_score) { it.toString() }

    val displayVerse = Transformations.map(gameViewModel.puzzle) {
        "${it?.verse?.book} ${it?.verse?.chapter}:${it?.verse?.verse}"
    }

    private val _route = MutableLiveData<Route?>()
    val route: LiveData<Route?> = _route

    val words: LiveData<List<Word>?> = Transformations.map(gameViewModel.puzzle) {
        it?.verse?.words
    }

    private val scoreSyncObserver = Observer<Int> {
        _score.postValue(it + gameViewModel.session.value!!.progress.totalScore)
    }

    private val puzzleStateObserver = Observer<Puzzle?> {
        updateRoute()
        if (it != null) {
            observeScore()
        }
    }

    private fun observeScore() {
        gameViewModel.puzzle.value!!.score.observeForever(scoreSyncObserver)
    }

    private fun updateRoute() {
        _route.value = when (gameViewModel.puzzle.value) {
            is LinkClearPuzzle -> Route.LINK_CLEAR
            else -> Route.LOADER
        }
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
