package mg.maniry.tenymana.ui.views.verse

import android.content.Context
import android.util.AttributeSet
import mg.maniry.tenymana.ui.game.colors.GameColors

class VerseView : BaseVerseView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = VerseViewControl()

    override fun onColorsChanged(colors: GameColors) {
        super.onColorsChanged(colors)
        invalidate()
    }

    override fun reRender() {
        invalidate()
    }
}
