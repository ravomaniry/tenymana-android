package mg.maniry.tenymana.gameLogic.linkClear

import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.directions
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle.Companion.gravity
import mg.maniry.tenymana.gameLogic.models.*
import mg.maniry.tenymana.gameLogic.shared.grid.calcScoredMoves
import mg.maniry.tenymana.gameLogic.shared.grid.getRandomByRate
import mg.maniry.tenymana.gameLogic.shared.grid.placeWord
import mg.maniry.tenymana.utils.Random
import kotlin.math.max

fun buildLinkGrid(
    verse: BibleVerse,
    random: Random,
    gridWidth: Int,
    gridHeight: Int
): Pair<Grid<CharAddress>, List<SolutionItem<CharAddress>>> {
    val grid = MutableGrid<CharAddress>(gridWidth)
    val solution = mutableListOf<SolutionItem<CharAddress>>()
    val validWordLen = 2..(max(gridHeight, gridWidth))
    val words = verse.uniqueWords.filter { it.size in validWordLen }.toMutableList()
    while (words.isNotEmpty()) {
        val word = random.from(words)
        val moves = grid.calcScoredMoves(gridHeight, word, directions, gravity)
        if (moves.isNotEmpty()) {
            val move = moves.getRandomByRate(random)
            grid.placeWord(move.a, move.b, word, gravity)
            solution.add(SolutionItem(grid.toGrid(), move.toPoints(word.size)))
        }
        words.remove(word)
    }
    return Pair(grid.toGrid(), solution)
}

private fun Move.toPoints(len: Int): List<Point> {
    val points = mutableListOf(a)
    for (i in 1 until len) {
        points.add(a + b * i)
    }
    return points
}
