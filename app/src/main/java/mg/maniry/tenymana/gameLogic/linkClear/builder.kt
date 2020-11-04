package mg.maniry.tenymana.gameLogic.linkClear

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.directions
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Grid
import mg.maniry.tenymana.gameLogic.models.MutableGrid
import mg.maniry.tenymana.gameLogic.shared.grid.calcScoredMoves
import mg.maniry.tenymana.gameLogic.shared.grid.getRandomByRate
import mg.maniry.tenymana.gameLogic.shared.grid.placeWord
import mg.maniry.tenymana.utils.Random

fun buildLinkGrid(verse: BibleVerse, random: Random, width: Int, visibleH: Int): Grid<CharAddress> {
    val grid = MutableGrid<CharAddress>(width)
    val words = verse.uniqueWords.filter { it.size > 1 }.toMutableList()
    while (words.isNotEmpty()) {
        val word = random.from(words)
        val moves = grid.calcScoredMoves(visibleH, word, directions, gravity)
        if (moves.isNotEmpty()) {
            val move = moves.getRandomByRate(random)
            grid.placeWord(move.a, move.b, word, gravity)
        }
        words.remove(word)
    }
    return grid.toGrid()
}
