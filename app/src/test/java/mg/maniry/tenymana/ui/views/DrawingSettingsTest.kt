package mg.maniry.tenymana.ui.views

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.ui.views.settings.DrawingSettingsObserver
import mg.maniry.tenymana.utils.verifyOnce
import mg.maniry.tenymana.utils.verifyTimes
import org.junit.Test

class DrawingSettingsTest {
    @Test
    fun updteAndNotify() {
        val settings = DrawingSettings()
        val verseViewSub: DrawingSettingsObserver = mock()
        val charGridSub0: DrawingSettingsObserver = mock()
        val charGridSub1: DrawingSettingsObserver = mock()
        settings.subscribe(DrawingSettings.Event.VERSE_VIEW, verseViewSub)
        settings.subscribe(DrawingSettings.Event.CHAR_GRID, charGridSub0)
        settings.subscribe(DrawingSettings.Event.CHAR_GRID, charGridSub1)
        // Chars grid
        //      - subscribe & notify
        settings.charGridOrigin = Point(2, 2)
        settings.charGridCellSize = 10f
        assertThat(settings.charGridOrigin).isEqualTo(Point(2, 2))
        assertThat(settings.charGridCellSize).isEqualTo(10)
        verifyTimes(charGridSub0, 2).onDrawingSettingsChanged()
        verifyTimes(charGridSub1, 2).onDrawingSettingsChanged()
        verifyZeroInteractions(verseViewSub)
        //      - forget
        clearInvocations(charGridSub0, charGridSub1)
        settings.forget(DrawingSettings.Event.CHAR_GRID, charGridSub0)
        settings.charGridCellSize = 20f
        verifyZeroInteractions(charGridSub0)
        verifyOnce(charGridSub1).onDrawingSettingsChanged()
        verifyZeroInteractions(verseViewSub)
        // Verse view
        settings.verseViewHeight = 50
        verifyOnce(verseViewSub).onDrawingSettingsChanged()
        clearInvocations(verseViewSub)
        settings.forget(DrawingSettings.Event.VERSE_VIEW, verseViewSub)
        settings.verseViewHeight = 50
        verifyZeroInteractions(verseViewSub)
    }
}
