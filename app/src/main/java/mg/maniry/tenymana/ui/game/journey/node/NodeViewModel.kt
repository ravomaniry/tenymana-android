package mg.maniry.tenymana.ui.game.journey.node

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.game.models.BibleVerse
import mg.maniry.tenymana.ui.DefaultColor
import mg.maniry.tenymana.ui.GameColors

class NodeViewModel : ViewModel() {
    private val _color = MutableLiveData<GameColors>(DefaultColor)
    val colors: LiveData<GameColors> = _color

    private val _score = MutableLiveData<Int>()
    val score: LiveData<String> = Transformations.map(_score) { it.toString() }

    private val _verse = MutableLiveData<BibleVerse>()
    val displayVerse = Transformations.map(_verse) { "${it.book} ${it.chapter}:${it.verse}" }
}
