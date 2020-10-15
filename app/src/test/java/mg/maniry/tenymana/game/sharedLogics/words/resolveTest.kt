package mg.maniry.tenymana.game.sharedLogics.words

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.BibleVerse
import mg.maniry.tenymana.game.models.Character
import org.junit.Test

class ResolveTest {
    private val verse = BibleVerse.fromText("Matio", 1, 1, "Abc de ghi àbc jkl")
    private val initialStatus = listOf(false, true, false, true, false, true, false, true, false)

    @Test
    fun resolveSingle() {
        testResolve(
            setOf(),
            listOf(Character('D', 'd'), Character('E', 'e')),
            listOf(2)
        )
    }

    @Test
    fun resolveDouble() {
        testResolve(
            setOf(),
            listOf(Character('A', 'a'), Character('B', 'b'), Character('C', 'c')),
            listOf(0, 6)
        )
    }

    @Test
    fun resolveHiddenAt0() {
        testResolve(
            setOf(0),
            listOf(Character('D', 'd'), Character('E', 'e')),
            listOf(2, 0)
        )
    }

    @Test
    fun resolveHiddenInMiddle() {
        testResolve(
            setOf(2, 6),
            listOf(Character('G', 'g'), Character('H', 'h'), Character('I', 'i')),
            listOf(4, 6)
        )
    }

    @Test
    fun resolveHiddenLastWord() {
        testResolve(
            setOf(8),
            listOf(Character('A', 'a'), Character('B', 'b'), Character('C', 'c')),
            listOf(0, 6, 8)
        )
    }

    @Test
    fun resolveMultipleHidden() {
        testResolve(
            setOf(2, 4, 8),
            listOf(Character('A', 'a'), Character('B', 'b'), Character('C', 'c')),
            listOf(0, 6, 2, 4, 8)
        )
    }

    private fun testResolve(
        hidden: Set<Int>,
        chars: List<Character>,
        result: List<Int>
    ) {
        val words = verse.words.toMutableList()
        val indexes = words.resolveWith(chars, hidden)
        assertThat(indexes).isEqualTo(result)
        val status = initialStatus.toMutableList()
        result.forEach { status[it] = true }
        for (w in words) {
            assertThat(w.resolved).isEqualTo(status[w.index])
        }
    }
}
