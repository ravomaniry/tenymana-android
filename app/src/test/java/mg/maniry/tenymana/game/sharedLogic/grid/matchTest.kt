package mg.maniry.tenymana.game.sharedLogic.grid

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.BibleVerse
import mg.maniry.tenymana.game.models.Character
import mg.maniry.tenymana.game.models.Grid
import mg.maniry.tenymana.game.models.Move
import mg.maniry.tenymana.game.models.Point.Companion.directions
import org.junit.Test

class MatchTest {
    @Test
    fun left_right() {
        val words = BibleVerse.fromText("Matio", 1, 1, "Abc de fg ij").words.toMutableList()
        val grid = Grid(
            listOf(
                listOf(c('a'), c('b'), c('c')),
                listOf(c('a'), c('e'), c('d')),
                listOf(c('g'), c('f'), null)
            )
        )
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(0, 0, 2, 0))
        // [0] is resolved
        words[0] = words[0].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(2, 1, 1, 1))
        // [0], [2] resolved + off grid
        words[2] = words[2].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isNull()
        // [0], [2] resolved on grid
        assertThat(grid.firstVisibleMatch(words, 3, directions)).isEqualTo(Move.xy(1, 2, 0, 2))
        // [0], [2], [4] resolved
        words[4] = words[4].resolvedVersion
        assertThat(grid.firstVisibleMatch(words, 4, directions)).isNull()
    }

    @Test
    fun upDown_gridH_limit() {
        val words = BibleVerse.fromText("", 1, 1, "Abc de fg").words
        val grid = Grid(
            listOf(
                listOf(c('a'), c('e')),
                listOf(c('b'), c('d')),
                listOf(c('c'), null)
            )
        )
        // ABC off grid
        assertThat(grid.firstVisibleMatch(words, 2, directions)).isEqualTo(Move.xy(1, 1, 1, 0))
        // ABC in grid
        assertThat(grid.firstVisibleMatch(words, 3, directions)).isEqualTo(Move.xy(0, 0, 0, 2))
    }

    private fun c(v: Char) = Character(v.toUpperCase(), v.toLowerCase())
}
