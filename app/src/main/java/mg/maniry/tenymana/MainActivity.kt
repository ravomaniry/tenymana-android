package mg.maniry.tenymana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.ui.app.AppViewModel
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSingletons()
    }

    private fun initSingletons() {
        val viewModels: SharedViewModels by inject()
        viewModels.app = ViewModelProvider(this).get(AppViewModel::class.java)
    }
}
