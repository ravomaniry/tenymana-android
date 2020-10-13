package mg.maniry.tenymana.game.linkClear

import mg.maniry.tenymana.game.models.*
import mg.maniry.tenymana.game.sharedLogics.grid.calcDirections
import mg.maniry.tenymana.game.sharedLogics.grid.calcOrigins
import mg.maniry.tenymana.game.sharedLogics.grid.placeWord
import mg.maniry.tenymana.game.sharedLogics.grid.toCharGrid
import mg.maniry.tenymana.utils.InsideOutIterator
import mg.maniry.tenymana.utils.Random

fun buildLinkGrid(verse: BibleVerse, random: Random, width: Int, visibleH: Int): Grid<Character> {
    val grid = MutableGrid<CharAddress>(width)
    val words = verse.uniqueWords.toMutableList()
    while (words.isNotEmpty()) {
        var persited = false
        val word = random.from(words)
        val origins = grid.calcOrigins(visibleH)
        val originsIt = InsideOutIterator.random(origins, random)
        while (originsIt.hasNext && !persited) {
            val o = originsIt.next()
            val dirs = grid.calcDirections(Point.directions, o, word, visibleH)
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
