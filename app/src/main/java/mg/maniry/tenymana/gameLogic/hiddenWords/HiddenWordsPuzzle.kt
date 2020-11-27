package mg.maniry.tenymana.gameLogic.hiddenWords

import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.gameLogic.models.Puzzle

interface HiddenWordsPuzzle : Puzzle {
    val groups: List<HiddenWordsGroup>
    fun propose(group: Int, charsIndexes: List<Int>): Boolean
}

class HiddenWordsPuzzleImpl(
    initialVerse: BibleVerse,
    initialGroups: List<HiddenWordsGroup>
) : HiddenWordsPuzzle {
    override val score = MutableLiveData(0)
    override val completed = false
    private val words = initialVerse.words.toMutableList()
    override val verse: BibleVerse = initialVerse.copy(words = words)
    override val groups = initialGroups.toMutableList()

    override fun propose(group: Int, charsIndexes: List<Int>): Boolean {
        TODO("Not yet implemented")
    }
}
