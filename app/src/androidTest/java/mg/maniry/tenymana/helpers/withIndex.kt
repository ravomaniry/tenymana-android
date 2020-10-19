package mg.maniry.tenymana.helpers

import android.annotation.SuppressLint
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
    return object : TypeSafeMatcher<View?>() {
        var currentIndex = 0
        var viewObjHash = 0

        @SuppressLint("DefaultLocale")
        override fun describeTo(description: Description) {
            description.appendText(String.format("with index: %d ", index))
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            if (matcher.matches(view) && currentIndex++ == index) {
                viewObjHash = view.hashCode()
            }
            return view.hashCode() == viewObjHash
        }
    }
}
