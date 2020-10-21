package mg.maniry.tenymana.repositories.dao

import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Progress
import mg.maniry.tenymana.repositories.models.Session

private class Directories(userID: String) {
    val journey = "$userID/journey"
    val progress = "$userID/progress"
}

class GameDao(
    userID: String,
    private val fs: FsHelper
) {
    private val dirs = Directories(userID)

    suspend fun getSessions() = fs.readSessions(dirs)
}

private suspend fun FsHelper.readSessions(dirs: Directories): List<Session> {
    val sessions = mutableListOf<Session>()
    val fileNames = list(dirs.journey)
    for (fileName in fileNames) {
        val journey = readJson("${dirs.journey}/$fileName", Journey::class.java)
        if (journey != null) {
            val id = fileName.substring(0, fileName.lastIndexOf('.'))
            val progress =
                readJson("${dirs.progress}/$fileName", Progress::class.java) ?: Progress.empty(id)
            sessions.add(Session(journey, progress))
        }
    }
    return sessions
}
