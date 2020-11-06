package mg.maniry.tenymana.ui.game.puzzle.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.ui.game.colors.GameColors

open class CharGridBackground : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected val control = CharGridViewControl()

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings = settings
    }

    fun onGridChanged(grid: Grid<Character>?) {
        control.onGridChanged(grid)
        invalidate()
    }

    fun onVisibleHChanged(h: Int) {
        control.onVisibleHChanged(h)
        invalidate()
    }

    fun onColorsChanged(colors: GameColors) {
        control.onColorChanged(ContextCompat.getColor(context, colors.primary))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        control.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas, CharGridViewControl.DrawMode.BG)
        }
    }
}
