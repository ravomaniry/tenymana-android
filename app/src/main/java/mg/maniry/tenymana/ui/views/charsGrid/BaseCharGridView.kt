package mg.maniry.tenymana.ui.views.charsGrid

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.settings.DrawingSettings

abstract class BaseCharGridView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val control: BaseCharGridControl

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

    open fun onColorsChanged(colors: GameColors) {
        control.onColorChanged(ContextCompat.getColor(context, colors.primary))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        control.onSizeChanged(w, h)
    }
}
