package mg.maniry.tenymana.game.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BibleVerseTest {
    @Test
    fun split() {
        val verse = BibleVerse.fromText(
            book = "Matio",
            chapter = 4,
            verse = 10,
            text = "Azà menatra. (jer 1.30) Mijòrôa [Gr. teny]"
        )
        val words = listOf(
            Word(
                index = 0,
                value = "Azà",
                chars = listOf(
                    Character(value = 'A', compValue = 'a'),
                    Character(value = 'z', compValue = 'z'),
                    Character(value = 'à', compValue = 'a')
                )
            ),
            Word(
                index = 1,
                value = " ",
                isSeparator = true,
                resolved = true,
                chars = listOf(Character(value = ' ', compValue = ' '))
            ),
            Word(
                index = 2,
                value = "menatra",
                chars = listOf(
                    Character(value = 'm', compValue = 'm'),
                    Character(value = 'e', compValue = 'e'),
                    Character(value = 'n', compValue = 'n'),
                    Character(value = 'a', compValue = 'a'),
                    Character(value = 't', compValue = 't'),
                    Character(value = 'r', compValue = 'r'),
                    Character(value = 'a', compValue = 'a')
                )
            ),
            Word(
                index = 3,
                resolved = true,
                isSeparator = true,
                value = ". ",
                chars = listOf(
                    Character(value = '.', compValue = '.'),
                    Character(value = ' ', compValue = ' ')
                )
            ),
            Word(
                index = 4,
                value = "Mijòrôa",
                chars = listOf(
                    Character(value = 'M', compValue = 'm'),
                    Character(value = 'i', compValue = 'i'),
                    Character(value = 'j', compValue = 'j'),
                    Character(value = 'ò', compValue = 'o'),
                    Character(value = 'r', compValue = 'r'),
                    Character(value = 'ô', compValue = 'o'),
                    Character(value = 'a', compValue = 'a')
                )
            )
        )
        assertThat(verse).isEqualTo(
            BibleVerse(
                book = "Matio",
                chapter = 4,
                verse = 10,
                text = "Azà menatra. (jer 1.30) Mijòrôa [Gr. teny]",
                words = words
            )
        )
    }

    @Test
    fun doNotIgnoreIfAllIsIgnored() {
        val text = "(fa Izay niasa),"
        assertThat(BibleVerse.fromText("", 2, 1, text).words.size).isEqualTo(6)
    }
}
