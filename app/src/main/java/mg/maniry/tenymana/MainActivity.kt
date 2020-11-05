package mg.maniry.tenymana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSharedViewModels()
    }

    private fun initSharedViewModels() {
        val bibleRepo: BibleRepo by inject()
        val userRepo: UserRepo by inject()
        val viewModels: SharedViewModels by inject()
        viewModels.app = ViewModelProvider(this, AppViewModel.factory(bibleRepo, userRepo))
            .get(AppViewModel::class.java)
    }
}
