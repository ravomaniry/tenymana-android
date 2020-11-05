package mg.maniry.tenymana.gameLogic.models

import androidx.lifecycle.LiveData

interface Puzzle {
    val score: LiveData<Int>
    val completed: Boolean
    val verse: BibleVerse
    fun propose(move: Move): Boolean
    fun undo(): Boolean
}
