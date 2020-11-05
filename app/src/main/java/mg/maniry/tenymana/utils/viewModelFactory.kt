package mg.maniry.tenymana.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <V> newViewModelFactory(builder: () -> V): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("unchecked_cast")
            return builder() as T
        }
    }
}
