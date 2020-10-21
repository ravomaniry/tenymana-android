package mg.maniry.tenymana.gameLogic.models

import androidx.lifecycle.LiveData
import mg.maniry.tenymana.gameLogic.models.Move

interface Puzzle {
    val score: LiveData<Int>
    val completed: LiveData<Boolean>
    fun propose(move: Move): Boolean
}
