package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.game.models.Journey
import mg.maniry.tenymana.game.models.Progress
import mg.maniry.tenymana.game.models.Session

private const val JOURNEY_DIR = "journey"
private const val PROGRESS_DIR = "progress"

interface GameRepository {
    val sessions: LiveData<List<Session>>
}

class GameRepositoryImpl(
    private val fs: FsHelper
) : GameRepository {
    override val sessions = MutableLiveData<List<Session>>()

    suspend fun initialize() {
        sessions.postValue(fs.readSessions())
    }

}

private suspend fun FsHelper.readSessions(): List<Session> {
    val sessions = mutableListOf<Session>()
    val fileNames = list(JOURNEY_DIR)
    for (fileName in fileNames) {
        val journey = readJson("$JOURNEY_DIR/$fileName", Journey::class.java)
        if (journey != null) {
            val id = fileName.substring(0, fileName.lastIndexOf('.'))
            val progress = readJson("$PROGRESS_DIR/$fileName", Progress::class.java)
            sessions.add(Session(journey, progress ?: Progress.empty(id)))
        }
    }
    return sessions
}
