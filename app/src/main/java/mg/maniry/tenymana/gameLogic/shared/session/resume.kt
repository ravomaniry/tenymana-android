package mg.maniry.tenymana.gameLogic.shared.session

import mg.maniry.tenymana.gameLogic.models.BibleVerse
import mg.maniry.tenymana.repositories.models.Session

data class SessionItem(
    val pathIndex: Int,
    val verseIndex: Int,
    val verse: BibleVerse
)

fun Session.resume() {

}
