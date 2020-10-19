package mg.maniry.tenymana.game.models

import androidx.lifecycle.LiveData

interface Puzzle {
    val score: LiveData<Int>
    val completed: LiveData<Boolean>
    fun propose(move: Move): Boolean
}
