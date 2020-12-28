package mg.maniry.tenymana.ui.views.containers

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class FixedWLinearLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    var parentW = 100
        set(value) {
            if (value > 0) {
                field = value
                requestLayout()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(parentW, heightMeasureSpec)
    }
}
