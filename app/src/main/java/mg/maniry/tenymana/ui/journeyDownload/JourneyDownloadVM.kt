package mg.maniry.tenymana.ui.journeyDownload

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.R
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.ApiJourneyListItem
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.newViewModelFactory

class JourneyDownloadVM(
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val kDispatchers: KDispatchers
) : ViewModel() {
    private var pageI = 0
    val list = MutableLiveData<List<ApiJourneyListItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val hasPrev = MutableLiveData(false)
    val hasNext = MutableLiveData(false)
    val message = MutableLiveData<Int?>(null)
    val downloaded = MutableLiveData(mutableSetOf<String>())

    private val userObs = Observer<User?> {
        if (it != null) {
            viewModelScope.launch(kDispatchers.main) {
                gameRepo.initialize(it.id)
            }
        }
    }

    private val sessionsObs = Observer<List<Session>> {
        it?.forEach { session ->
            downloaded.value?.add(session.journey.id)
        }
    }

    fun loadPrevPage() {
        if (pageI > 0) {
            pageI--
            fetchList()
        }
    }

    fun loadNextPage() {
        if (hasNext.value == true) {
            pageI++
            fetchList()
        }
    }

    fun onClose() {
        gameRepo.cancelAllRequests()
    }

    fun refreshList() {
        pageI = 0
        fetchList()
    }

    fun download(item: ApiJourneyListItem) {
        wrapRequest {
            val content = gameRepo.fetchJourney(item.key)
            gameRepo.saveJourney(content)
            downloaded.value?.add(item.id)
            downloaded.postValue(downloaded.value)
            message.postValue(R.string.journey_dld_downloaded)
        }
    }

    private fun fetchList() {
        wrapRequest {
            hasPrev.postValue(false)
            hasNext.postValue(false)
            val result = gameRepo.fetchJourneysList(pageI)
            list.postValue(result.list)
            hasPrev.postValue(pageI > 0)
            hasNext.postValue(result.hasNext)
        }
    }

    private fun wrapRequest(fn: suspend () -> Unit) {
        viewModelScope.launch(kDispatchers.main) {
            try {
                message.postValue(null)
                isLoading.postValue(true)
                fn()
            } catch (e: Exception) {
                message.postValue(R.string.journey_dld_net_err)
                Log.e("JourneyDownload", "$e")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    init {
        userRepo.user.observeForever(userObs)
        gameRepo.sessions.observeForever(sessionsObs)
    }

    override fun onCleared() {
        super.onCleared()
        userRepo.user.removeObserver(userObs)
        gameRepo.sessions.removeObserver(sessionsObs)
    }

    companion object {
        fun factory(userRepo: UserRepo, gameRepo: GameRepo, kDispatchers: KDispatchers) =
            newViewModelFactory { JourneyDownloadVM(userRepo, gameRepo, kDispatchers) }
    }
}
