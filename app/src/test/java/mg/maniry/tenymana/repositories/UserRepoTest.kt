package mg.maniry.tenymana.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.api.FsHelper
import mg.maniry.tenymana.repositories.models.User
import mg.maniry.tenymana.utils.DateTimeUtil
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Rule
import org.junit.Test

class UserRepoTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun setup_newUser() {
        val defUser = User("1000-11-22", "Teny Mana")
        testUserRepo(
            1000,
            listOf(11, 22),
            repoUsers = listOf(defUser),
            activeUser = defUser,
            save = true
        )
    }

    @Test
    fun setup_userExists() {
        val savedUsers = SavedUsers(
            listOf(
                User("111-11-11", "User 0"),
                User("222-22-22", "User 1")
            )
        )
        testUserRepo(
            savedUsers = savedUsers,
            repoUsers = savedUsers.values,
            activeUser = savedUsers.values[0]
        )
    }

    private fun testUserRepo(
        now: Long = 0,
        randoms: List<Int> = listOf(0),
        savedUsers: SavedUsers? = null,
        repoUsers: List<User>,
        activeUser: User,
        save: Boolean = false
    ) {
        val random: Random = mock {
            on { int(0, 1_000_000) } doReturnConsecutively randoms
        }
        val dt: DateTimeUtil = mock {
            on { nowTst } doReturn now
        }
        val fs: FsHelper = mock {
            onBlocking { readJson("users.json", SavedUsers::class.java) } doReturn savedUsers
        }
        val repo = UserRepoImpl(fs, random, dt)
        // No value by default
        assertThat(repo.users.value).isNull()
        // setup
        runBlocking {
            repo.setup()
            // load users
            verifyOnce(fs).readJson("users.json", SavedUsers::class.java)
            // generate new user + save + activate
            assertThat(repo.users.value).isEqualTo(repoUsers)
            assertThat(repo.user.value).isEqualTo(activeUser)
            if (save) {
                verifyOnce(fs).writeJson(
                    "users.json",
                    SavedUsers(repoUsers),
                    SavedUsers::class.java
                )
            } else {
                verifyNever(fs).writeJson(
                    "users.json",
                    SavedUsers(repoUsers),
                    SavedUsers::class.java
                )
            }
        }
    }
}
