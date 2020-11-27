package mg.maniry.tenymana.gameLogic.hiddenWords

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Character
import mg.maniry.tenymana.utils.Random
import org.junit.Test

class BuilderTest {
    @Test
    fun simpleCase_groupOf4() {
        val verse = BibleVerse.fromText("", 1, 1, "ab bc efg abc d fghi jk")
        val randomIndexes = listOf(2, 1)
        val randomInts = listOf(
            6, 5, 4, 3, 2, 1, 0,
            0, 0, 0
        )
        val groups = listOf(
            HiddenWordsGroup(chars('c', 'b', 'a', 'c', 'b', 'b', 'a'), verse.words[4]),
            HiddenWordsGroup(chars('d', 'j', 'k'), verse.words[10])
        )
        testBuildGroups(verse, randomIndexes, randomInts, groups)
    }

    private fun testBuildGroups(
        verse: BibleVerse,
        randomIndexes: List<Int>,
        randomInts: List<Int>,
        groups: List<HiddenWordsGroup>
    ) {
        val random: Random = mock {
            on { this.int(any(), any()) } doReturnConsecutively randomInts
            onGeneric { this.from(any<List<Int>>()) } doReturnConsecutively randomIndexes
        }
        assertThat(buildHiddenWordsGroups(verse, 4, random)).isEqualTo(groups)
    }

    fun chars(vararg values: Char): List<Character> {
        return values.map { Character(it.toUpperCase(), it.toLowerCase()) }
    }
}
