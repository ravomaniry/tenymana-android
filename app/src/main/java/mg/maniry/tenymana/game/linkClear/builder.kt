package mg.maniry.tenymana.game.linkClear

import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.sharedLogics.grid.applyGravity
import mg.maniry.tenymana.game.sharedLogics.grid.placeWord
import mg.maniry.tenymana.game.sharedLogics.grid.testChange
import mg.maniry.tenymana.game.sharedLogics.grid.toCharGrid
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random

fun buildLinkGrid(verse: BibleVerse, random: Random, w: Int): Grid<Character> {
    val grid = MutableGrid<CharAddress>(w)
    val words = verse.uniqueWords.toMutableList()
    while (words.isNotEmpty()) {
        var persited = false
        val word = random.from(words)
        val origins = grid.calcOrigins()
        val originsIt = InsideOutIterator.random(origins, random)
        while (originsIt.hasNext && !persited) {
            val o = originsIt.next()
            val dirs = grid.calcDirections(o, word)
            if (dirs.isNotEmpty()) {
                val dir = random.from(dirs)
                grid.placeWord(o, dir, word)
                persited = true
            }
        }
        words.remove(word)
    }
    return grid.toCharGrid(verse.words)
}

private fun Grid<CharAddress>.calcOrigins(): MutableList<Point> {
    val starts = mutableListOf<Point>()
    forEach { x, y, p ->
        if (y == 0 || p != null || get(x, y - 1) != null) {
            starts.add(Point(x, y))
        }
    }
    return starts
}

private fun Grid<CharAddress>.calcDirections(origin: Point, word: Word): List<Point> {
    val dirs = mutableListOf<Point>()
    for (dir in directions) {
        if (dirIsPossible(origin, dir, word)) {
            dirs.add(dir)
        }
    }
    return dirs
}

private fun Grid<CharAddress>.dirIsPossible(origin: Point, dir: Point, word: Word): Boolean {
    for (di in 0 until word.size) {
        val p = origin + (dir * di)
        if (!canContain(p) || this[p] != null) {
            return false
        }
    }
    val test = testChange(origin, dir, word)
    val withG = test.toMutable().apply { applyGravity(gravity) }
    return test == withG
}
