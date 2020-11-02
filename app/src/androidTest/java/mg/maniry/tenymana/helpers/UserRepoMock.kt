package mg.maniry.tenymana.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.User

class UserRepoMock : UserRepo {
    val usersM = MutableLiveData<List<User>?>(null)
    override val users: LiveData<List<User>?> = usersM

    val userM = MutableLiveData<User?>(null)
    override val user: LiveData<User?> = userM

    val setupFn = Fn<Unit>()
    override suspend fun setup() {
        setupFn()
    }
}
