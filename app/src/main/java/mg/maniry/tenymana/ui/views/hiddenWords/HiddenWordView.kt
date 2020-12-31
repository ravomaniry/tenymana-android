package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup
import mg.maniry.tenymana.ui.game.colors.GameColors

class HiddenWordView : BaseHiddenWordsView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = HiddenWordViewControl()

    fun onColorsChange(colors: GameColors) {
        control.onColorsChange(
            ResourcesCompat.getColor(resources, colors.accent, null),
            ResourcesCompat.getColor(resources, colors.primary, null)
        )
    }

    fun onResolved(resolved: Boolean) {
        if (control.resolved != resolved) {
            control.resolved = resolved
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas)
        }
    }
}

@BindingAdapter("hiddenWordsGroup")
fun HiddenWordView.bindGroup(group: HiddenWordsGroup?) {
    if (group != null) {
        onWordChange(group.hidden.chars)
        onResolved(group.resolved)
    }
}

@BindingAdapter("gameColors")
fun HiddenWordView.bindGameColors(colors: GameColors?) {
    if (colors != null) {
        onColorsChange(colors)
    }
}
