package mg.maniry.tenymana.ui.bible

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

class BibleTest : KoinTest {
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
            val matio1 = listOf("Matio 1.1", "Matio 1.2", "Matio 1.3")
            val marka2 = listOf("Marka 2.1", "Marka 2.2")
            val bibleRepo: BibleRepo by inject()
            whenever(bibleRepo.getBooks()).thenReturn(books)
            whenever(bibleRepo.getChapter("Matio", 1)).thenReturn(matio1)
            whenever(bibleRepo.getChapter("Marka", 2)).thenReturn(marka2)
            // Render
            ActivityScenario.launch(MainActivity::class.java)
            // Go to bible scren
            clickView(R.id.goToBibleBtn)
            assertShouldBeVisible(R.id.bibleScreen)
            // Show form but no chapter select - no chapter display
            assertShouldBeVisible(R.id.bibleBooksList)
            assertShouldBeInvisible(R.id.bibleChaptersList)
            assertShouldBeInvisible(R.id.bibleVerseRef)
            assertShouldBeInvisible(R.id.bibleVerses)
            assertShouldHaveText(R.id.bibleBookNameItem, 0, "Matio")
            assertShouldHaveText(R.id.bibleBookNameItem, 1, "Marka")
            // Select book : show chapters
            clickViewWithText("Matio")
            assertShouldBeVisible(R.id.bibleChaptersList)
            assertShouldHaveText(R.id.bibleVerseRef, 0, "Matio: ")
            assertShouldBeInvisible(R.id.bibleVerses)
            clickViewWithText("1")
            assertShouldHaveText(R.id.bibleVerseRef, 0, "Matio: 1")
            assertShouldBeInvisible(R.id.bibleBooksList)
            assertShouldBeInvisible(R.id.bibleChaptersList)
            assertShouldBeVisible(R.id.bibleVerses)
            assertShouldHaveText(R.id.solutionScreenVerseItem, 0, "1- Matio 1.1")
            assertShouldHaveText(R.id.solutionScreenVerseItem, 1, "2- Matio 1.2")
            // Close: go to home
            clickView(R.id.bibleCloseBtn)
            assertShouldBeVisible(R.id.goToBibleBtn)
        }
    }
}
