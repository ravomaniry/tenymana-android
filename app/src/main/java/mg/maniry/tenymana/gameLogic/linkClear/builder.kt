package mg.maniry.tenymana.gameLogic.linkClear

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.MutableGrid
import mg.maniry.tenymana.gameLogic.shared.grid.calcDirections
import mg.maniry.tenymana.gameLogic.shared.grid.calcOrigins
import mg.maniry.tenymana.gameLogic.shared.grid.placeWord
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random

fun buildLinkGrid(verse: BibleVerse, random: Random, width: Int, visibleH: Int): Grid<CharAddress> {
    val grid =
        MutableGrid<CharAddress>(width)
    val words = verse.uniqueWords.filter { it.size > 1 }.toMutableList()
    while (words.isNotEmpty()) {
        var persisted = false
        val word = random.from(words)
        val origins = grid.calcOrigins(visibleH)
        val originsIt = InsideOutIterator.random(origins, random)
        while (originsIt.hasNext && !persisted) {
            val o = originsIt.next()
            val dirs = grid.calcDirections(LinkClearPuzzle.directions, o, word, visibleH, gravity)
            if (dirs.isNotEmpty()) {
                val dir = random.from(dirs)
                grid.placeWord(o, dir, word, gravity)
                persisted = true
            }
        }
        words.remove(word)
    }
    return grid.toGrid()
}
