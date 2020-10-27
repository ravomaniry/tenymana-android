package mg.maniry.tenymana.ui.game.puzzle.views

import android.view.View
import mg.maniry.tenymana.gameLogic.models.Point

class DrawingSettings {
    private val charGridSubs = mutableSetOf<View>()
    private val verseViewSubs = mutableSetOf<View>()

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

    private var _verseViewHeight = 0
    var verseViewHeight: Int
        get() = _verseViewHeight
        set(value) {
            if (_verseViewHeight != value) {
                _verseViewHeight = value
                notify(verseViewSubs)
            }
        }

    fun subscribe(e: Event, view: View) {
        when (e) {
            Event.CHAR_GRID -> charGridSubs.add(view)
            Event.VERSE_VIEW -> verseViewSubs.add(view)
        }
    }

    fun forget(e: Event, view: View) {
        when (e) {
            Event.CHAR_GRID -> charGridSubs.remove(view)
            Event.VERSE_VIEW -> verseViewSubs.remove(view)
        }
    }

    private fun notify(views: Set<View>) {
        for (v in views) {
            v.invalidate()
        }
    }

    enum class Event {
        CHAR_GRID,
        VERSE_VIEW
    }
}
