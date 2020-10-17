package mg.maniry.tenymana.game.models

import androidx.lifecycle.MutableLiveData

interface Board {
    val score: MutableLiveData<Int>
    fun propose(move: Move): Boolean
}
