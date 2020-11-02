package mg.maniry.tenymana.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.BibleRepo

class AppViewModelFactory(
    private val bibleRepo: BibleRepo
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppViewModel(bibleRepo) as T
    }
}
