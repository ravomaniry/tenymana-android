package mg.maniry.tenymana.ui.game.puzzle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mg.maniry.tenymana.gameLogic.models.Puzzle
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.ui.game.GameViewModel
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PuzzleViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun bonusOne() {
        testCanUseBonus(0, 0, false)
        testCanUseBonus(PuzzleViewModel.bonusOnePrice, 0, true)
        testCanUseBonus(PuzzleViewModel.bonusOnePrice - 1, 1, true)
        testCanUseBonus(PuzzleViewModel.bonusOnePrice, -PuzzleViewModel.bonusOnePrice, false)
    }

    private fun testCanUseBonus(
        puzzleScore: Int,
        progressScore: Int,
        bonusOne: Boolean
    ) {
        val score = MutableLiveData(puzzleScore)
        val puzzle: Puzzle = mock {
            on { this.score } doAnswer { score }
        }
        val puzzleLD = MutableLiveData(puzzle)
        val session = Session(
            Journey("0"),
            Progress("0", progressScore, emptyList(), emptyList())
        )
        val sessionLD = MutableLiveData(session)
        val gameVm: GameViewModel = mock {
            on { this.puzzle } doReturn puzzleLD
            on { this.session } doReturn sessionLD
        }
        val viewModel = PuzzleViewModel(gameVm)
        assertThat(viewModel.canUseBonusOne()).isEqualTo(bonusOne)
    }
}
