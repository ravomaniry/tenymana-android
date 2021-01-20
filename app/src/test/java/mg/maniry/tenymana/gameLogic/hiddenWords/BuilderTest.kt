package mg.maniry.tenymana.gameLogic.hiddenWords

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.utils.*
import org.junit.Test

class BuilderTest {
    @Test
    fun simpleCase_groupOf4() {
        val verse = BibleVerse.fromText("", 1, 1, "ab bc efg abc d fghi jk")
        val hiddenIndexes = listOf(2, 1)
        val charIndexes = listOf(
            6, 5, 4, 3, 2, 1, 0,
            0, 0, 0
        )
        val groups = listOf(
            HiddenWordsGroup(chars('c', 'b', 'a', 'c', 'b', 'b', 'a'), verse.words[4]),
            HiddenWordsGroup(chars('d', 'j', 'k'), verse.words[10])
        )
        val random: Random = mock {
            on { this.int(any(), any()) } doReturnConsecutively charIndexes
            onGeneric { this.from(any<List<Int>>()) } doReturnConsecutively hiddenIndexes
        }
        assertThat(buildHiddenWordsGroups(verse, 4, random)).isEqualTo(groups)
        verifyOnce(random).from(listOf(2, 3))
        verifyOnce(random).from(listOf(1))
        verifyOnce(random).int(0, 6)
        verifyOnce(random).int(0, 5)
        verifyOnce(random).int(0, 4)
        verifyOnce(random).int(0, 3)
        verifyTimes(random, 2).int(0, 2)
        verifyTimes(random, 2).int(0, 1)
        verifyTimes(random, 2).int(0, 0)
    }

    @Test
    fun sizePlusTwo() {
        val verse = BibleVerse.fromText("", 1, 1, "aaa bbb ccc ddd eee fff gg hh")
        val groups = buildHiddenWordsGroups(verse, 3, RandomImpl())
        assertThat(groups.size).isEqualTo(2)
    }

    @Test
    fun uniqueWordsOnly() {
        val verse = BibleVerse.fromText("", 1, 1, "aaa aaa bb")
        val grous = buildHiddenWordsGroups(verse, 4, RandomImpl())
        assertThat(grous[0].hidden).isEqualTo(verse.words[4])
    }
}
