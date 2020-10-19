package mg.maniry.tenymana.helpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.not

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

fun shouldBeVisible(vararg ids: Int) {
    for (id in ids) {
        findView(id, null).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}

fun shouldBeInvisible(vararg ids: Int) {
    for (id in ids) {
        findView(id, null).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

fun shouldHaveTextID(id: Int, index: Int? = null, resID: Int) {
    shouldBeVisible(id)
    findView(id, null).check(matches(withText(resID)))
}

fun shouldHaveText(id: Int, index: Int? = null, text: String) {
    findView(id, index).check(matches(withText(text)))
}

fun clickView(id: Int, index: Int? = null) {
    findView(id, index).apply {
        scrollIfNeeded(this)
        perform(click())
    }
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

fun shouldBeEnabled(id: Int, index: Int? = null) {
    findView(id, index).check(matches(isEnabled()))
}

fun shouldBeDisabled(id: Int, index: Int? = null) {
    findView(id, index).check(matches(not(isEnabled())))
}
