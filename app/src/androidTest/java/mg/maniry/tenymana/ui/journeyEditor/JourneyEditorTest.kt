package mg.maniry.tenymana.ui.journeyEditor

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.runBlocking
import mg.maniry.tenymana.MainActivity
import mg.maniry.tenymana.R
import mg.maniry.tenymana.helpers.*
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.Book
import mg.maniry.tenymana.repositories.UserRepo
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
            whenever(userRepo.user).thenReturn(MutableLiveData(User("1", "")))
            // Bible repo
            val books = listOf(Book("Matio", 10), Book("Marka", 5))
            val bibleRepo: BibleRepo by inject()
            whenever(bibleRepo.getBooks()).thenReturn(books)
            // Render
            ActivityScenario.launch(MainActivity::class.java)
            // -> go to editor screen
            clickView(R.id.goToJourneyEditorBtn)
            assertShouldBeVisible(R.id.journeyEditorScreen)
            // cancel
            clickView(R.id.jEditor_cancel_summary)
            assertShouldBeVisible(R.id.goToJourneyEditorBtn)
            // Open and edit
            clickView(R.id.goToJourneyEditorBtn)
            assertShouldBeVisible(R.id.journeyNoPathHint)
            // Enter name and description
            typeInTextView(R.id.journeyNameValue, text = "Joureney title")
            typeInTextView(R.id.journeyDescriptionValue, text = "Joureney desc", closeKB = true)
            clickView(R.id.journeyNoPathHint)
            assertShouldBeVisible(R.id.pathTitleValue)
        }
    }
}
