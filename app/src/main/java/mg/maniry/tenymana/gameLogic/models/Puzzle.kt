package mg.maniry.tenymana.gameLogic.models

import androidx.lifecycle.LiveData

interface Puzzle {
    val score: LiveData<Int>
    val completed: LiveData<Boolean>
    val verse: BibleVerse
    fun propose(move: Move): Boolean
}
