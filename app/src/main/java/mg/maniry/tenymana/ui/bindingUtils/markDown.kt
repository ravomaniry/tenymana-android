package mg.maniry.tenymana.ui.bindingUtils

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.ITALIC
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import mg.maniry.tenymana.R

data class MdSpans(
    val text: String,
    val styles: List<SpanStyle>
) {
    enum class Type {
        Headline,
        Bold,
        Italic,
        Plain
    }

    data class SpanStyle(val type: Type, val start: Int, val end: Int)

    override fun toString(): String {
        return "text='$text'\nstyles=[${styles.joinToString("\n")}]"
    }
}

@BindingAdapter("markdown")
fun TextView.bindMarkdown(value: String?) {
    if (value == null) {
        text = ""
    } else {
        val md = parseMarkdown(value)
        val textSpan = SpannableStringBuilder(md.text)
        val headlineColor = ResourcesCompat.getColor(resources, R.color.red, null)
        val boldColor = ResourcesCompat.getColor(resources, R.color.blueDark, null)
        for (style in md.styles) {
            val spansTypes = when (style.type) {
                MdSpans.Type.Headline -> listOf(
                    ForegroundColorSpan(headlineColor),
                    StyleSpan(BOLD),
                    RelativeSizeSpan(1.5f)
                )
                MdSpans.Type.Bold -> listOf(StyleSpan(BOLD), ForegroundColorSpan(boldColor))
                MdSpans.Type.Italic -> listOf(StyleSpan(ITALIC))
                MdSpans.Type.Plain -> emptyList()
            }
            for (span in spansTypes) {
                textSpan.setSpan(span, style.start, style.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        text = textSpan
    }
}

fun parseMarkdown(value: String): MdSpans {
    var text = ""
    var tmpValue = ""
    var tmpStyle = MdSpans.Type.Plain
    val styles = mutableListOf<MdSpans.SpanStyle>()
    var prevChar: Char = ' '

    fun append() {
        if (tmpValue.isNotEmpty()) {
            if (tmpValue.isWhiteSpace() && styles.isNotEmpty()) {
                val l = styles.size
                styles[l - 1] = styles[l - 1].copy(end = styles[l - 1].end + tmpValue.length)
            } else {
                styles.add(MdSpans.SpanStyle(tmpStyle, text.length, text.length + tmpValue.length))
            }
            text += tmpValue
        }
        prevChar = ' '
        tmpValue = ""
    }

    fun toggleStyle(next: MdSpans.Type) {
        tmpStyle = if (tmpStyle == next) MdSpans.Type.Plain else next
    }

    for (c in value) {
        when {
            c == '\n' -> {
                tmpValue += c
                append()
                tmpStyle = MdSpans.Type.Plain
            }
            c == '#' && prevChar == '#' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append()
                toggleStyle(MdSpans.Type.Headline)
            }
            c == '*' && prevChar == '*' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append()
                toggleStyle(MdSpans.Type.Bold)
            }
            c == '_' && prevChar == '_' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append()
                toggleStyle(MdSpans.Type.Italic)
            }
            else -> {
                tmpValue += c
            }
        }
        prevChar = c
    }
    append()
    return MdSpans(text, styles)
}

private fun String.isWhiteSpace(): Boolean {
    for (i in this) {
        if (i != '\n' && i != ' ') {
            return false
        }
    }
    return true
}
