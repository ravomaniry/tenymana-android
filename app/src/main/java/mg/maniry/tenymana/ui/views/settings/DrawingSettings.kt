package mg.maniry.tenymana.ui.views.settings

import mg.maniry.tenymana.gameLogic.models.Point
import mg.maniry.tenymana.ui.views.verse.VerseViewControl

class DrawingSettings {
    private val charGridSubs = mutableSetOf<DrawingSettingsObserver>()
    private val verseViewSubs = mutableSetOf<DrawingSettingsObserver>()

    private var _charGridOrigin = Point(0, 0)
    var charGridOrigin: Point
        get() = _charGridOrigin
        set(value) {
            if (value != _charGridOrigin) {
                _charGridOrigin = value
                notify(charGridSubs)
            }
        }

    private var _charGridCellSize = 0f
    var charGridCellSize: Float
        get() = _charGridCellSize
        set(value) {
            if (_charGridCellSize != value) {
                _charGridCellSize = value
                notify(charGridSubs)
            }
        }

    private var _verseViewHeight = VerseViewControl.PADDING * 2
    var verseViewHeight: Int
        get() = _verseViewHeight
        set(value) {
            if (_verseViewHeight != value) {
                _verseViewHeight = value
                notify(verseViewSubs)
            }
        }

    fun subscribe(e: Event, observer: DrawingSettingsObserver) {
        when (e) {
            Event.CHAR_GRID -> charGridSubs.add(observer)
            Event.VERSE_VIEW -> verseViewSubs.add(observer)
        }
    }

    fun forget(e: Event, observer: DrawingSettingsObserver) {
        when (e) {
            Event.CHAR_GRID -> charGridSubs.remove(observer)
            Event.VERSE_VIEW -> verseViewSubs.remove(observer)
        }
    }

    private fun notify(observers: MutableSet<DrawingSettingsObserver>) {
        for (v in observers) {
            v.onDrawingSettingsChanged()
        }
    }

    enum class Event {
        CHAR_GRID,
        VERSE_VIEW
    }
}
