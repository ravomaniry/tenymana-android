package mg.maniry.tenymana.ui.game.journey

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.clickView
import mg.maniry.tenymana.helpers.shouldBeVisible
import mg.maniry.tenymana.ui.game.puzzle.PuzzleFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @Test
    fun linkClear() {
        launchFragmentInContainer<PuzzleFragment>()
        shouldBeVisible(R.id.verseDisplay)
        shouldBeVisible(R.id.bonusOkBtn)
        // Go to puzzle screen
        clickView(R.id.bonusOkBtn)
        shouldBeVisible(R.id.linkClearPuzzle)
    }
}
