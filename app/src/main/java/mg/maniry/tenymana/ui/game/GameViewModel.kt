package mg.maniry.tenymana.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.game.models.BibleVerse
import mg.maniry.tenymana.ui.GameColors

class GameViewModel : ViewModel() {
    private val _color = MutableLiveData<GameColors>()
    val colors: LiveData<GameColors> = _color

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _verse = MutableLiveData<BibleVerse>()
    val displayVerse = Transformations.map(_verse) { "${it.book} ${it.chapter}:${it.verse}" }

}
