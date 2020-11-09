package mg.maniry.tenymana.ui.views.bonusBtn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import mg.maniry.tenymana.ui.game.colors.GameColors

abstract class BonusBtnView : AppCompatImageButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val price: String

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = 20f
        color = Color.BLACK
    }

    fun onColorsChanged(colors: GameColors) {
        paint.color = ResourcesCompat.getColor(resources, colors.accent, null)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.textSize = h.toFloat() / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(price, 2f, height.toFloat() - 2f, paint)
    }
}
