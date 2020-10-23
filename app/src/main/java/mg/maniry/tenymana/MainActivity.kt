package mg.maniry.tenymana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.gamesList.GameViewModel
import mg.maniry.tenymana.ui.gamesList.GameViewModelFactory
import mg.maniry.tenymana.utils.Random
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSingletons()
    }

    private fun initSingletons() {
        val viewModels: SharedViewModels by inject()
        val userRepo: UserRepo by inject()
        val gameRepo: GameRepo by inject()
        val random: Random by inject()
        viewModels.app = ViewModelProvider(this).get(AppViewModel::class.java)
        val fct = GameViewModelFactory(viewModels.app, userRepo, gameRepo, random)
        viewModels.game = ViewModelProvider(this, fct).get(GameViewModel::class.java)
    }
}
