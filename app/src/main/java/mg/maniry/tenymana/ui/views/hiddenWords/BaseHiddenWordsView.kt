package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.ui.game.colors.GameColors

abstract class BaseHiddenWordsView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val control: BaseHiddenWordsViewControl

    abstract fun onColorsChange(colors: GameColors)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        control.onMeasure(width)
        setMeasuredDimension(width, control.height)
    }

    fun onWordChange(word: List<Character?>) {
        val prevH = control.height
        control.word = word
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

@BindingAdapter("gameColors")
fun BaseHiddenWordsView.bindGameColors(colors: GameColors?) {
    if (colors != null) {
        onColorsChange(colors)
    }
}
