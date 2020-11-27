package mg.maniry.tenymana.gameLogic.hiddenWords

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle

interface HiddenWordsPuzzle : Puzzle {
    fun propose(group: Int, charsIndexes: List<Int>)
}

class HiddenWordsPuzzleImpl(
    initialVerse: BibleVerse,
    initialGroups: List<HiddenWordsGroup>
) : HiddenWordsPuzzle {
    override val score = MutableLiveData(0)
    override val completed = false
    override val verse: BibleVerse = initialVerse

    override fun propose(group: Int, charsIndexes: List<Int>) {
        TODO("Not yet implemented")
    }
}
