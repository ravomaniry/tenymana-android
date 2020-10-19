package mg.maniry.tenymana.game.puzzles

import androidx.lifecycle.LiveData
import mg.maniry.tenymana.game.models.Move

interface Puzzle {
    val score: LiveData<Int>
    val completed: LiveData<Boolean>
    fun propose(move: Move): Boolean
}
