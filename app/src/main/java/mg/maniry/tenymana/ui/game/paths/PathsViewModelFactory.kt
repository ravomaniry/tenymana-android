package mg.maniry.tenymana.ui.game.paths

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.ui.game.GameViewModel

class PathsViewModelFactory(
    private val gameViewModel: GameViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return PathsViewModel(gameViewModel) as T
    }
}
