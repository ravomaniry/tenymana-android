package mg.maniry.tenymana.game.linkClear

import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.sharedLogics.applyGravity
import mg.maniry.tenymana.game.sharedLogics.placeWord
import mg.maniry.tenymana.game.sharedLogics.testChange
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random

fun buildLinkGrid(verse: BibleVerse, random: Random, w: Int): Grid {
    val grid = MutableGrid(w)
    val words = verse.uniqueWords.toMutableList()
    while (words.isNotEmpty()) {
        var persited = false
        val word = random.from(words)
        val origins = listOrigins(grid)
        val originsIt = InsideOutIterator.random(origins, random)
        while (originsIt.hasNext && !persited) {
            val o = originsIt.next()
            val dirs = listPossibleDirs(grid, o, word)
            if (dirs.isNotEmpty()) {
                val dir = random.from(dirs)
                grid.placeWord(o, dir, word)
                persited = true
            }
        }
        words.remove(word)
    }
    return grid.toGrid()
}

private fun listOrigins(grid: Grid): MutableList<Point> {
    val starts = mutableListOf<Point>()
    grid.forEach { x, y, p ->
        if (y == 0 || p != null || grid[x, y - 1] != null) {
            starts.add(Point(x, y))
        }
    }
    return starts
}

private fun listPossibleDirs(grid: Grid, origin: Point, word: Word): List<Point> {
    val dirs = mutableListOf<Point>()
    for (dir in directions) {
        if (dirIsPossible(grid, origin, dir, word)) {
            dirs.add(dir)
        }
    }
    return dirs
}

private fun dirIsPossible(grid: Grid, origin: Point, dir: Point, word: Word): Boolean {
    for (di in 0 until word.size) {
        val p = origin + (dir * di)
        if (!grid.canContain(p) || grid[p] != null) {
            return false
        }
    }
    val test = grid.testChange(origin, dir, word)
    val withG = test.toMutable().apply { applyGravity(gravity) }
    return test == withG
}
