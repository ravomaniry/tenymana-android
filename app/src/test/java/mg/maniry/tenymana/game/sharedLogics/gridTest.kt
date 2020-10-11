package mg.maniry.tenymana.game.sharedLogics

import com.google.common.truth.Truth.assertThat
import mg.maniry.tenymana.game.models.*
import org.junit.Test

class GridLogicTest {
    @Test
    fun applyGravity_Down() {
        val grid = MutableGrid(4).apply {
            set(1, 1, Point(0, 0))
            set(2, 0, Point(1, 1))
            applyGravity(listOf(DOWN))
        }
        assertThat(grid).isEqualTo(
            MutableGrid(
                4,
                mutableListOf(
                    mutableListOf(null, Point(0, 0), Point(1, 1), null),
                    mutableListOf<Point?>(null, null, null, null),
                    mutableListOf<Point?>(null, null, null, null)
                )
            )
        )
    }

    @Test
    fun applyGravity_DownLeft() {
        val grid = MutableGrid(4).apply {
            set(0, 3, Point(2, 0))
            set(1, 1, Point(0, 0))
            set(3, 0, Point(1, 0))
            set(3, 2, Point(1, 1))
            set(3, 3, Point(1, 2))
            applyGravity(listOf(DOWN, LEFT))
        }
        assertThat(grid).isEqualTo(
            MutableGrid(
                4,
                mutableListOf(
                    mutableListOf(Point(2, 0), Point(0, 0), Point(1, 0), null),
                    mutableListOf(Point(1, 1), null, null, null),
                    mutableListOf(Point(1, 2), null, null, null),
                    mutableListOf<Point?>(null, null, null, null),
                    mutableListOf<Point?>(null, null, null, null)
                )
            )
        )
    }

    @Test
    fun persistUp() {
        val word = Word.fromValue("Abc", 0, false)
        val grid = MutableGrid(3).apply {
            persist(Point(0, 0), UP, word)
        }
        assertThat(grid).isEqualTo(
            MutableGrid(
                3,
                mutableListOf(
                    mutableListOf(Point(0, 0), null, null),
                    mutableListOf(Point(0, 1), null, null),
                    mutableListOf(Point(0, 2), null, null),
                    mutableListOf<Point?>(null, null, null)
                )
            )
        )
    }

    @Test
    fun persistLeft() {
        val word = Word.fromValue("Abc", 0, false)
        val grid = MutableGrid(3).apply {
            persist(Point(2, 0), LEFT, word)
        }
        assertThat(grid).isEqualTo(
            MutableGrid(
                3,
                mutableListOf(
                    mutableListOf<Point?>(Point(0, 2), Point(0, 1), Point(0, 0)),
                    mutableListOf<Point?>(null, null, null)
                )
            )
        )
    }
}
