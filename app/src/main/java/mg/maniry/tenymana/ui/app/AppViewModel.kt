package mg.maniry.tenymana.ui.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Screen {
    HOME,
    GAMES,
    PUZZLE
}

class AppViewModel : ViewModel() {
    val screen = MutableLiveData<Screen>(Screen.HOME)
}
