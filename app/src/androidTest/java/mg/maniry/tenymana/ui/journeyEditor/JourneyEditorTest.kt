package mg.maniry.tenymana.ui.journeyEditor

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.Book
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.models.Journey
import mg.maniry.tenymana.repositories.models.Path
import mg.maniry.tenymana.repositories.models.User
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class JourneyEditorTest : KoinTest {
    @Before
    fun iniKoin() {
        setupTestKoin()
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun test() {
        runBlocking {
            // user mock
            val userRepo: UserRepo by inject()
            whenever(userRepo.user).thenReturn(MutableLiveData(User("1000", "")))
            // Bible repo
            val books = listOf(Book("Matio", 10), Book("Marka", 5))
            val bibleRepo: BibleRepo by inject()
            val gameRepo: GameRepo by inject()
            whenever(bibleRepo.getBooks()).thenReturn(books)
            whenever(bibleRepo.getChapter("Matio", 1)).thenReturn(listOf("1", "2"))
            whenever(bibleRepo.getChapter("Marka", 1)).thenReturn(listOf("1"))
            whenever(bibleRepo.getChapter("Marka", 5)).thenReturn(listOf("1", "2", "3", "4", "5"))
            // Render
            ActivityScenario.launch(MainActivity::class.java)
            // -> go to editor screen
            clickView(R.id.goToJourneyEditorBtn)
            assertShouldBeVisible(R.id.journeyEditorScreen)
            // Init repos
            verifyOnce(userRepo).setup()
            verifyOnce(gameRepo).initialize("1000")
            // can not submit
            assertShouldBeInvisible(R.id.jEditor_submit_summary)
            // cancel
            clickView(R.id.jEditor_cancel_summary)
            assertShouldBeVisible(R.id.goToJourneyEditorBtn)
            // Go Journey editor and edit
            clickView(R.id.goToJourneyEditorBtn)
            assertShouldBeVisible(R.id.journeyNoPathHint)
            // Enter name and description
            typeInTextView(R.id.journeyNameValue, text = "Journey title")
            typeInTextView(R.id.journeyDescriptionValue, text = "Journey desc", closeKB = true)
            // Go to path editor and cancel
            clickView(R.id.journeyNoPathHint)
            clickView(R.id.jEditor_cancel_path)
            // Go to path editor and edit
            clickView(R.id.jEditor_addPathBtn)
            assertShouldBeVisible(R.id.pathTitleValue)
            // Save default values
            typeInTextView(R.id.pathTitleValue, text = "Path 1")
            // Display summary: enable submit btn + display path item
            clickView(R.id.jEditor_submit_path)
            assertShouldBeVisible(R.id.jEditor_path_preview)
            assertShouldHaveText(R.id.jEditor_path_previw_title, text = "Path 1")
            // create on more
            clickView(R.id.jEditor_addPathBtn)
            assertShouldHaveText(R.id.pathTitleValue, text = "")
            typeInTextView(R.id.pathTitleValue, text = "Path 2")
            clickView(R.id.jEditor_submit_path)
            // displays
            assertShouldHaveText(R.id.jEditor_path_previw_title, 0, text = "Path 1")
            assertShouldHaveText(R.id.jEditor_path_previw_title, 1, text = "Path 2")
            // Delete paths
            clickView(R.id.deletePathBtn, 1)
            assertShouldHaveText(R.id.jEditor_path_previw_title, text = "Path 1")
            clickView(R.id.deletePathBtn)
            assertShouldBeInvisible(R.id.jEditor_submit_summary)
            // Add + interract with form
            clickView(R.id.jEditor_addPathBtn)
            clickView(R.id.pathBookValue)
            clickViewWithText("Marka")
            clickView(R.id.pathChapterValue)
            clickViewWithText("5")
            clickView(R.id.pathStartVerseValue)
            clickViewWithText("2")
            clickView(R.id.pathEndVerseValue)
            clickViewWithText("4")
            assertShouldBeInvisible(R.id.jEditor_submit_path)
            typeInTextView(R.id.pathTitleValue, text = "Path 1")
            // submit
            clickView(R.id.jEditor_submit_path)
            // Add another and edit
            clickView(R.id.jEditor_addPathBtn)
            typeInTextView(R.id.pathTitleValue, text = "Path 2", closeKB = true)
            clickView(R.id.pathBookValue)
            clickViewWithText("Matio")
            clickView(R.id.pathStartVerseValue)
            clickViewWithText("2")
            clickView(R.id.jEditor_submit_path)
            clickView(R.id.jEditor_editPathBtn, 1)
            assertShouldHaveText(R.id.pathTitleValue, text = "Path 2")
            typeInTextView(R.id.pathTitleValue, text = "Path 2 update", closeKB = true)
            clickView(R.id.pathStartVerseValue)
            clickViewWithText("1")
            clickView(R.id.pathEndVerseValue)
            clickViewWithText("2")
            clickView(R.id.jEditor_submit_path)
            // Submit -> save journey -> go to home screen
            clickView(R.id.jEditor_submit_summary)
            verifyOnce(gameRepo).saveNewJourney(
                Journey(
                    "",
                    "Journey title",
                    "Journey desc",
                    listOf(
                        Path("Path 1", "", "Marka", 5, 2, 4),
                        Path("Path 2 update", "", "Matio", 1, 1, 2)
                    )
                )
            )
            assertShouldBeVisible(R.id.goToJourneyEditorBtn)
        }
    }
}
