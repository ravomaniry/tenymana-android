package mg.maniry.tenymana.repositories.dao

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session

private class Directories(userID: String) {
    val journey = "$userID/journey"
    val progress = "$userID/progress"
}

class SessionDao(
    userID: String,
    private val fs: FsHelper
) {
    private val dirs = Directories(userID)

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
        return sessions
    }

    suspend fun saveProgress(progress: Progress) {
        fs.writeJson("${dirs.progress}/${progress.journeyID}.json", progress, Progress::class.java)
    }
}
