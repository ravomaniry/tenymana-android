package mg.maniry.tenymana.ui.bindingUtils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MarkDownTest {
    @Test
    fun commonCases() {
        val raw = "##headline\nraw txt. **bold** __italic__ **bold\n*pla_i **bld2** raw"
        assertThat(parseMarkdown(raw)).isEqualTo(
            MdSpans(
                text = "headline\nraw txt. bold italic bold\n*pla_i bld2 raw",
                styles = listOf(
                    MdSpans.SpanStyle(MdSpans.Type.Headline, 0, 9),
                    MdSpans.SpanStyle(MdSpans.Type.Plain, 9, 18),
                    MdSpans.SpanStyle(MdSpans.Type.Bold, 18, 23),
                    MdSpans.SpanStyle(MdSpans.Type.Italic, 23, 30),
                    MdSpans.SpanStyle(MdSpans.Type.Bold, 30, 35),
                    MdSpans.SpanStyle(MdSpans.Type.Plain, 35, 42),
                    MdSpans.SpanStyle(MdSpans.Type.Bold, 42, 46),
                    MdSpans.SpanStyle(MdSpans.Type.Plain, 46, 50)
                )
            )
        )
    }
}
