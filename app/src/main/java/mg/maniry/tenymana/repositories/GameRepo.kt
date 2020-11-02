package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.dao.Directories
import mg.maniry.tenymana.repositories.dao.SessionDao
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.setupUtils.copyAssets

interface GameRepo {
    val sessions: LiveData<List<Session>>
    suspend fun initialize(userID: String)
    fun saveProgress(progress: Progress)
}

class GameRepoImpl(
    private val fs: FsHelper
) : GameRepo {
    override val sessions = MutableLiveData<List<Session>>()
    private lateinit var dao: SessionDao

    override suspend fun initialize(userID: String) {
        dao = SessionDao(userID, fs)
        val savedSessions = dao.getSessions()
        if (savedSessions.isEmpty()) {
            fs.copyAssets(Directories.JOURNEY_DIR, dao.dirs.journey)
            sessions.postValue(dao.getSessions())
        } else {
            sessions.postValue(savedSessions)
        }
    }

    override fun saveProgress(progress: Progress) {
        GlobalScope.launch {
            dao.saveProgress(progress)
        }
    }
}
