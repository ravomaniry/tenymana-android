package mg.maniry.tenymana.ui.bindingUtils

import android.widget.TextView
import androidx.databinding.BindingAdapter

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

}

fun parseMarkdown(value: String): MdSpans {
    var text = ""
    var tmpValue = ""
    var tmpStyle = MdSpans.Type.Plain
    val styles = mutableListOf<MdSpans.SpanStyle>()
    var prevChar: Char = ' '

    fun append(style: MdSpans.Type) {
        if (tmpValue.isNotEmpty()) {
            if (tmpValue.isWhiteSpace() && styles.isNotEmpty()) {
                val l = styles.size
                styles[l - 1] = styles[l - 1].copy(end = styles[l - 1].end + tmpValue.length)
            } else {
                styles.add(
                    MdSpans.SpanStyle(
                        tmpStyle,
                        text.length,
                        text.length + tmpValue.length - 1
                    )
                )
            }
            tmpStyle = MdSpans.Type.Plain
            text += tmpValue
        }
        tmpStyle = style
        prevChar = ' '
        tmpValue = ""
    }

    for (c in value) {
        when {
            c == '\n' -> {
                tmpValue += c
                append(MdSpans.Type.Plain)
            }
            c == '#' && prevChar == '#' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append(MdSpans.Type.Headline)
            }
            c == '*' && prevChar == '*' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append(MdSpans.Type.Bold)
            }
            c == '_' && prevChar == '_' -> {
                tmpValue = tmpValue.substring(0, tmpValue.length - 1)
                append(MdSpans.Type.Italic)
            }
            else -> {
                tmpValue += c
            }
        }
        prevChar = c
    }
    append(MdSpans.Type.Plain)
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
