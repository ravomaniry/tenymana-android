package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class HiddenWordsViewModel(
    puzzleViewModel: PuzzleViewModel,
    kDispatchers: KDispatchers
) : ViewModel() {

    companion object {
        fun factory(puzzleViewModel: PuzzleViewModel, kDispatchers: KDispatchers) =
            newViewModelFactory { HiddenWordsViewModel(puzzleViewModel, kDispatchers) }
    }
}
