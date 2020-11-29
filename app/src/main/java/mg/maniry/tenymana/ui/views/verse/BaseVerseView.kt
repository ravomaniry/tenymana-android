package mg.maniry.tenymana.ui.views.verse

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.game.colors.GameColors
import mg.maniry.tenymana.ui.views.settings.DrawingSettings

abstract class BaseVerseView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    protected abstract val control: BaseVerseViewControl

    abstract fun reRender()

    fun onWordsChange(words: List<Word>?) {
        control.onWordsChange(words)
        requestLayout()
    }

    fun onSettingsChanged(settings: DrawingSettings) {
        control.settings = settings
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        control.onMeasure(w)
        setMeasuredDimension(w, control.settings?.verseViewHeight ?: 0)
    }

    open fun onColorsChanged(colors: GameColors) {
        control.onColorsChange(
            ContextCompat.getColor(context, colors.primary),
            ContextCompat.getColor(context, colors.accent)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            control.draw(canvas)
        }
    }
}
