package mg.maniry.tenymana.ui.views.bonusBtn

import android.content.Context
import android.util.AttributeSet
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel

class BonusOneButtonView : BonusBtnView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val price = PuzzleViewModel.bonusOnePrice.toString()
}
