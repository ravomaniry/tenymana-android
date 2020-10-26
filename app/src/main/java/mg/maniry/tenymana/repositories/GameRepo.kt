package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.dao.SessionDao
import mg.maniry.tenymana.repositories.models.Session

interface GameRepo {
    val sessions: LiveData<List<Session>>
    suspend fun initialize(userID: String)
}

class GameRepoImpl(
    private val fs: FsHelper
) : GameRepo {
    override val sessions = MutableLiveData<List<Session>>()
    private lateinit var dao: SessionDao

    override suspend fun initialize(userID: String) {
        dao = SessionDao(userID, fs)
        sessions.postValue(dao.getSessions())
    }
}