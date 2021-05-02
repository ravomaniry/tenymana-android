package mg.maniry.tenymana.ui.journeysDownload

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class JourneysDownloadTest : KoinTest {
    @Before
    fun iniKoin() {
        setupTestKoin()
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun fullFlow() {
        runBlocking {
            // user mock
            val userRepo: UserRepo by inject()
            whenever(userRepo.user).thenReturn(MutableLiveData(User("1000", "")))
            // Game repo
            val gameRepo: GameRepo by inject()
            whenever(gameRepo.sessions).thenReturn(
                MutableLiveData(
                    listOf(Session(journey = Journey("id0"), progress = Progress("id0")))
                )
            )
            whenever(gameRepo.fetchJourneysList(0)).thenReturn(
                ApiJourneyListResult(
                    list = listOf(
                        ApiJourneyListItem("id0", "title0", "desc0", "key0"),
                        ApiJourneyListItem("id1", "title1", "desc1", "key1")
                    ),
                    hasNext = true
                )
            )
            whenever(gameRepo.fetchJourneysList(1)).thenReturn(
                ApiJourneyListResult(
                    listOf(ApiJourneyListItem("id2", "title2", "desc2", "key2")),
                    hasNext = false
                )
            )
            whenever(gameRepo.fetchJourney("key1")).thenReturn(Journey("id1"))
            whenever(gameRepo.fetchJourney("key2")).thenReturn(Journey("id2"))
            // Mount & go to journey download screen
            lateinit var resources: Resources
            ActivityScenario.launch(MainActivity::class.java).onActivity {
                resources = it.resources
            }
            clickView(R.id.goToJourneyDownload)
            assertShouldBeVisible(R.id.journeyDownloadScreen)
            // Init repos
            verifyOnce(userRepo).setup()
            verifyOnce(gameRepo).initialize("1000")
            // Load data
            verifyOnce(gameRepo).fetchJourneysList(0)
            // cancel & re-open: load data again
            clickView(R.id.journeyDldCloseBtn)
            verifyOnce(gameRepo).cancelAllRequests()
            clickView(R.id.goToJourneyDownload)
            verifyTimes(gameRepo, 2).fetchJourneysList(0)
            // Display journeys list && buttons
            assertShouldHaveText(R.id.jdItemTitle, 0, "title0")
            assertShouldHaveText(R.id.jdItemTitle, 1, "title1")
            assertShouldBeVisibleNth(R.id.jdDownloadedImg, 0)
            assertShouldBeInvisibleNth(R.id.jdDownloadedImg, 1)
            assertShouldBeVisible(R.id.journeyDldNextPageBtn)
            assertShouldBeInvisible(R.id.journeyDldPrevPageBtn)
            assertShouldBeInvisible(R.id.journeyDldMsg)
            // next page
            clickView(R.id.journeyDldNextPageBtn)
            assertShouldBeVisible(R.id.journeyDldPrevPageBtn)
            assertShouldBeInvisible(R.id.journeyDldNextPageBtn)
            assertShouldHaveText(R.id.jdItemTitle, text = "title2")
            verifyTimes(gameRepo, 1).fetchJourneysList(1)
            // Refresh
            clickView(R.id.journalDldRefreshBtn)
            assertShouldHaveText(R.id.jdItemTitle, text = "title2")
            verifyTimes(gameRepo, 2).fetchJourneysList(1)
            // download
            clickView(R.id.jdItem)
            verifyOnce(gameRepo).saveJourney(Journey("id2"))
            assertShouldHaveText(
                R.id.journeyDldMsg,
                text = resources.getString(R.string.journey_dld_downloaded)
            )
            // Go to prev page -> clear msg
            clickView(R.id.journeyDldPrevPageBtn)
            assertShouldBeInvisible(R.id.journeyDldMsg)
            // download
            clickView(R.id.jdItem, 1)
            verifyOnce(gameRepo).saveJourney(Journey("id1"))
        }
    }
}
