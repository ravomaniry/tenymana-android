package mg.maniry.tenymana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.AppViewModelFactory
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSingletons()
    }

    private fun initSingletons() {
        val bibleRepo: BibleRepo by inject()
        val viewModels: SharedViewModels by inject()
        val factory = AppViewModelFactory(bibleRepo)
        viewModels.app = ViewModelProvider(this, factory).get(AppViewModel::class.java)
    }
}
