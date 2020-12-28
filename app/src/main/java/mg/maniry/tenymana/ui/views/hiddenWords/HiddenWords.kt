package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.ui.game.colors.GameColors

class HiddenWordsView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private val control = HiddenWordsControl()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        control.onMeasure(width)
        setMeasuredDimension(width, control.height)
    }

    fun onColorsChange(colors: GameColors) {
        control.onColorsChange(
            ResourcesCompat.getColor(resources, colors.accent, null),
            ResourcesCompat.getColor(resources, colors.primary, null)
        )
    }

    fun onChange(word: List<Character?>, resolved: Boolean) {
        val prevH = control.height
        control.word = word
        control.resolved = resolved
        if (prevH == control.height) {
            invalidate()
        } else {
            requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas)
        }
    }
}
