package mg.maniry.tenymana.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.ui.app.AppViewModel

class HomeViewModelFactory(
    private val appViewModel: AppViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return HomeViewModel(appViewModel) as T
    }
}
