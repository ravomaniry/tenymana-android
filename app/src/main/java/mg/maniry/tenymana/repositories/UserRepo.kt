package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.utils.DateTimeUtil
import mg.maniry.tenymana.utils.Random

data class SavedUsers(val values: List<User>)

interface UserRepo {
    val users: LiveData<List<User>?>
    val user: LiveData<User?>
    suspend fun setup()
}

class UserRepoImpl(
    private val fs: FsHelper,
    private val random: Random,
    private val dateTimeUtil: DateTimeUtil
) : UserRepo {
    private val fileName = "users.json"

    override val users = MutableLiveData<List<User>?>(null)
    override val user = MutableLiveData<User?>(null)

    override suspend fun setup() {
        val saved = fs.readJson(fileName, SavedUsers::class.java)
        if (saved == null) {
            val defUser = newUser()
            users.postValue(listOf(defUser))
            user.postValue(defUser)
            fs.writeJson(fileName, SavedUsers(listOf(defUser)), SavedUsers::class.java)
        } else {
            users.postValue(saved.values)
            user.postValue(saved.values[0])
        }
    }

    private fun newUser(): User {
        val maxRand = 1_000_000
        val id = "${dateTimeUtil.nowTst}-${random.int(0, maxRand)}-${random.int(0, maxRand)}"
        return User(id, "Teny Mana")
    }
}

