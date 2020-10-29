package mg.maniry.tenymana.ui.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.utils.Random
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    fun navigation() {
        val route = MutableLiveData<Screen>(Screen.GAMES_LIST)
        val appViewModel: AppViewModel = mock { }
        // User repo
        val user = MutableLiveData<User>(User("1", "abc"))
        val userRepo: UserRepo = mock {
            on { this.user } doReturn user
        }
        //
        val gameRepo: GameRepo = mock { }
        val bibleRepo: BibleRepo = mock { }
        val random: Random = mock { }
        val viewModel = GameViewModel(appViewModel, userRepo, gameRepo, bibleRepo, random)

    }
}
