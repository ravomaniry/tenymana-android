package mg.maniry.tenymana.gameLogic.anagram

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.chars
import org.junit.Test

@Suppress("unchecked_cast")
class BuilderTest {
    @Test
    fun builderTest() {
        val verse = BibleVerse.fromText("", 1, 1, "Abc de abc i")
        val random: Random = mock {
            on { int(any(), any()) } doAnswer { it.arguments.last() as Int }
        }
        val resule = buildAnagram(verse, random)
        assertThat(resule).isEqualTo(
            listOf(
                chars('e', 'd'),
                chars('c', 'b', 'a')
            )
        )
    }
}
