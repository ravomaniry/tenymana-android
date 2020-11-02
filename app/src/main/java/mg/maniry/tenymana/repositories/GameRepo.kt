package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.dao.SessionDao
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session

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
        val j = Journey(
            "test",
            "Test",
            "Journey test",
            paths = listOf(
                Path("Part 1", "...", "Matio", 1, 1, 10),
                Path("Part 1", "...", "Matio", 1, 11, 20)
            )
        )
        fs.writeJson("0/journey/test.json", j, Journey::class.java)
        sessions.postValue(dao.getSessions())
    }

    override fun saveProgress(progress: Progress) {
        GlobalScope.launch {
            dao.saveProgress(progress)
        }
    }
}
