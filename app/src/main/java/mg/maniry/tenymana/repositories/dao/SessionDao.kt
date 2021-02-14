package mg.maniry.tenymana.repositories.dao

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session

class Directories(userID: String) {
    val journey = "$userID/$JOURNEY_DIR"
    val progress = "$userID/$PROGRESS_DIR"

    companion object {
        const val JOURNEY_DIR = "journey"
        const val PROGRESS_DIR = "progress"
    }
}

class SessionDao(
    userID: String,
    private val fs: FsHelper
) {
    val dirs = Directories(userID)

    suspend fun getSessions(): List<Session> {
        val sessions = mutableListOf<Session>()
        val fileNames = fs.list(dirs.journey)
        for (fileName in fileNames) {
            val journey = fs.readJson("${dirs.journey}/$fileName", Journey::class.java)
            if (journey != null) {
                val id = fileName.substring(0, fileName.lastIndexOf('.'))
                val progress = fs.readJson("${dirs.progress}/$fileName", Progress::class.java)
                sessions.add(Session(journey, progress ?: Progress(id)))
            }
        }
        sessions.sortBy { -it.progress.lastUpdate }
        return sessions
    }

    suspend fun saveProgress(progress: Progress) {
        fs.writeJson("${dirs.progress}/${progress.journeyID}.json", progress, Progress::class.java)
    }

    suspend fun saveJourney(journey: Journey) {
        fs.writeJson("${dirs.journey}/${journey.id}.json", journey, Journey::class.java)
    }

    suspend fun deleteJourney(id: String): Boolean {
        return fs.delete("${dirs.journey}/${id}.json")
    }
}
