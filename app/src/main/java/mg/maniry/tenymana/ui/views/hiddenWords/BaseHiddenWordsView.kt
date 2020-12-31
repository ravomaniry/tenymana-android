package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import mg.maniry.tenymana.gameLogic.models.Character

abstract class BaseHiddenWordsView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val control: BaseHiddenWordsViewControl

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
}
