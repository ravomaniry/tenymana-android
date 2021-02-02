package mg.maniry.tenymana.helpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

private fun findView(id: Int, index: Int?): ViewInteraction {
    return if (index == null) onView(withId(id)) else onView(withIndex(withId(id), index))
}

private fun scrollIfNeeded(v: ViewInteraction) {
    try {
        v.perform(scrollTo())
    } catch (e: Exception) {
        println("View is not inside scrollView")
    }
}

fun assertShouldBeVisible(vararg ids: Int) {
    for (id in ids) {
        findView(id, null).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}

fun assertShouldBeInvisible(vararg ids: Int) {
    for (id in ids) {
        findView(id, null).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

fun assertShouldHaveText(id: Int, index: Int? = null, text: String) {
    findView(id, index).check(matches(withText(text)))
}

fun clickView(id: Int, index: Int? = null) {
    findView(id, index).apply {
        scrollIfNeeded(this)
        perform(click())
    }
}

fun clickViewWithText(text: String) {
    onView(withText(text)).apply {
        scrollIfNeeded(this)
        perform(click())
    }
}

fun swipeRight(id: Int, index: Int? = null) {
    findView(id, index).perform(swipeRight())
}

fun typeInTextView(id: Int, index: Int? = null, text: String, closeKB: Boolean = false) {
    findView(id, index).apply {
        scrollIfNeeded(this)
        perform(clearText(), typeText(text))
        if (closeKB) {
            perform(closeSoftKeyboard())
        }
    }
}
