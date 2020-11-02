package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.repositories.models.User

interface UserRepo {
    val users: LiveData<List<User>?>
    val user: LiveData<User?>
}

class UserRepoImpl : UserRepo {
    override val users = MutableLiveData<List<User>?>(null)
    override val user = MutableLiveData<User?>(null)

    init {
        user.postValue(User("0", "Marc"))
    }
}
