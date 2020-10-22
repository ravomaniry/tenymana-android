package mg.maniry.tenymana.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen

class HomeViewModel(
    private val appViewModel: AppViewModel
) : ViewModel() {
    val screen: LiveData<Screen> = appViewModel.screen
    var shouldNavigate = false

    fun goToGameScreen() {
        shouldNavigate = true
        appViewModel.screen.postValue(Screen.GAMES_LIST)
    }
}
