package mg.maniry.tenymana.repositories

import androidx.lifecycle.LiveData
import mg.maniry.tenymana.repositories.models.User

interface UserRepo {
    val users: LiveData<List<User>?>
}

