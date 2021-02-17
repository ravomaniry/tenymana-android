package mg.maniry.tenymana.ui.bindingUtils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MarkDownTest {
    @Test
    fun commonCases() {
        val raw = "##headline\nraw txt. **bold** __italic__ **bold\n*pla_i"
        assertThat(parseMarkdown(raw)).isEqualTo(
            MdSpans(
                text = "headline\nraw txt. bold italic bold\n*pla_i",
                styles = listOf(
                    MdSpans.SpanStyle(MdSpans.Type.Headline, 0, 8),
                    MdSpans.SpanStyle(MdSpans.Type.Plain, 9, 17),
                    MdSpans.SpanStyle(MdSpans.Type.Bold, 18, 22),
                    MdSpans.SpanStyle(MdSpans.Type.Italic, 23, 29),
                    MdSpans.SpanStyle(MdSpans.Type.Bold, 30, 34),
                    MdSpans.SpanStyle(MdSpans.Type.Plain, 35, 40)
                )
            )
        )
    }
}
