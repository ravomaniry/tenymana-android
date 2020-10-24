package mg.maniry.tenymana.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.utils.Random

class GameViewModelFactory(
    private val appViewModel: AppViewModel,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val bibleRepo: BibleRepo,
    private val random: Random
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return GameViewModel(
            appViewModel,
            userRepo,
            gameRepo,
            bibleRepo,
            random
        ) as T
    }
}
