package mg.maniry.tenymana.ui.game.puzzle.views

import android.view.View
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.utils.verifyOnce
import mg.maniry.tenymana.utils.verifyTimes
import org.junit.Test

class DrawingSettingsTest {
    @Test
    fun updteAndNotify() {
        val settings = DrawingSettings()
        val verseViewSub: View = mock()
        val charGridSub0: View = mock()
        val charGridSub1: View = mock()
        settings.subscribe(DrawingSettings.Event.VERSE_VIEW, verseViewSub)
        settings.subscribe(DrawingSettings.Event.CHAR_GRID, charGridSub0)
        settings.subscribe(DrawingSettings.Event.CHAR_GRID, charGridSub1)
        // Chars grid
        //      - subscribe & notify
        settings.charGridOrigin = Point(2, 2)
        settings.charGridCellSize = 10f
        assertThat(settings.charGridOrigin).isEqualTo(Point(2, 2))
        assertThat(settings.charGridCellSize).isEqualTo(10)
        verifyTimes(charGridSub0, 2).invalidate()
        verifyTimes(charGridSub1, 2).invalidate()
        verifyZeroInteractions(verseViewSub)
        //      - forget
        clearInvocations(charGridSub0, charGridSub1)
        settings.forget(DrawingSettings.Event.CHAR_GRID, charGridSub0)
        settings.charGridCellSize = 20f
        verifyZeroInteractions(charGridSub0)
        verifyOnce(charGridSub1).invalidate()
        verifyZeroInteractions(verseViewSub)
        // Verse view
        settings.verseViewHeight = 50
        verifyOnce(verseViewSub).invalidate()
        clearInvocations(verseViewSub)
        settings.forget(DrawingSettings.Event.VERSE_VIEW, verseViewSub)
        settings.verseViewHeight = 50
        verifyZeroInteractions(verseViewSub)
    }
}
