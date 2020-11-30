package mg.maniry.tenymana.ui.views.verse

import android.graphics.Canvas
import mg.maniry.tenymana.gameLogic.models.Word
import mg.maniry.tenymana.ui.views.verse.BaseVerseViewControl.Companion.W

class AnimVerseViewControl : BaseVerseViewControl() {
    private var highlights = listOf<Highlight>()
    private var resolved = listOf<Boolean>()
    private var t = 0.0
    private var t0: Long = 0

    override fun onWordsChange(words: List<Word>?) {
        super.onWordsChange(words)
        if (words != null) {
            resolved = words.toResolvedList()
        }
    }

    override fun onColorsChange(primary: Int, accent: Int) {
        placeHolderPaint.color = primary
    }

    fun startAnim(now: Long) {
        this.t0 = now
        updateHighlights()
        t = if (highlights.isNotEmpty()) 0.0 else 1.1
    }

    override fun draw(canvas: Canvas) {
        if (t < 1) {
            for (h in highlights) {
                canvas.drawHighlight(h)
            }
        }
    }

    fun onTick(now: Long): Boolean {
        t = (now - t0) / animDuration
        return t < 1
    }

    private fun updateHighlights() {
        val nextResolved = words?.toResolvedList()
        val next = mutableListOf<Highlight>()
        highlights = next
        if (nextResolved != null) {
            for (i in nextResolved.indices) {
                if (nextResolved[i] && !resolved[i]) {
                    next.addAll(cells.buildHighlights(i))
                }
            }
            resolved = nextResolved
        }
    }

    private fun Canvas.drawHighlight(highlight: Highlight) {
        val x = (highlight.x0 + t * (highlight.x1 - highlight.x0)).toFloat()
        drawPlaceHolder(this, highlight.cells[0], x - highlight.cells[0].x)
        for (cell in highlight.cells) {
            if (cell.x > x) {
                drawPlaceHolder(this, cell)
            }
        }
    }

    companion object {
        const val animDuration = 200.0
    }
}

private data class Highlight(
    val x0: Float,
    val x1: Float,
    val cells: List<VerseViewCell>
)

private fun List<Word>.toResolvedList(): List<Boolean> {
    return map { it.resolved }
}

private fun List<List<VerseViewCell>>.buildHighlights(wIndex: Int): List<Highlight> {
    var activeRow: List<VerseViewCell>? = null
    val highlighted = mutableListOf<List<VerseViewCell>>()
    var tmpCells = mutableListOf<VerseViewCell>()
    for (row in this) {
        for (cell in row) {
            if (cell.char.wIndex > wIndex) {
                break
            }
            if (cell.char.wIndex == wIndex) {
                if (activeRow != row) {
                    tmpCells = mutableListOf()
                    highlighted.add(tmpCells)
                    activeRow = row
                }
                tmpCells.add(cell)
            }
        }
    }
    return highlighted.map { it.toHighlight() }
}

private fun List<VerseViewCell>.toHighlight(): Highlight {
    return Highlight(get(0).x, last().x + W, this)
}
