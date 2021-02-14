package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.dao.Directories
import mg.maniry.tenymana.repositories.dao.SessionDao
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session
import mg.maniry.tenymana.repositories.setupUtils.copyAssets
import java.util.*

interface GameRepo {
    val sessions: LiveData<List<Session>>
    suspend fun initialize(userID: String)
    fun saveProgress(progress: Progress)
    fun saveJourney(journey: Journey)
    fun saveNewJourney(journey: Journey)
    suspend fun deleteJourney(id: String)
}

class GameRepoImpl(
    private val fs: FsHelper
) : GameRepo {
    override val sessions = MutableLiveData<List<Session>>()
    private lateinit var dao: SessionDao
    private var activeUserID = ""

    override suspend fun initialize(userID: String) {
        activeUserID = userID
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
        val toSave = progress.copy(lastUpdate = Date().time)
        GlobalScope.launch {
            dao.saveProgress(toSave)
        }
    }

    override fun saveJourney(journey: Journey) {
        GlobalScope.launch {
            dao.saveJourney(journey)
        }
    }

    override fun saveNewJourney(journey: Journey) {
        val toSave = journey.copy(id = "${activeUserID}_${Date().time}")
        saveJourney(toSave)
    }

    override suspend fun deleteJourney(id: String) {
        dao.deleteJourney(id)
    }
}
