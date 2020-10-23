package mg.maniry.tenymana.ui.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.ui.gamesList.GameViewModel

class PuzzleViewModelFactory(
    private val gameViewModel: GameViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return PuzzleViewModel(gameViewModel) as T
    }
}
