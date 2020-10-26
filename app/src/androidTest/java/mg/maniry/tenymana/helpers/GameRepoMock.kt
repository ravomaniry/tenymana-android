package mg.maniry.tenymana.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.models.Session

class GameRepoMock : GameRepo {
    val sessionsM = MutableLiveData<List<Session>>()
    val initFn = Fn<Unit>()

    override val sessions: LiveData<List<Session>> = sessionsM
    override suspend fun initialize(userID: String) = initFn(userID)
}