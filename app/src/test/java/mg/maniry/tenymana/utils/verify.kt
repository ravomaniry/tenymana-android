package mg.maniry.tenymana.utils

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify

fun <T> verifyOnce(mock: T): T {
    return verify(mock, times(1))
}

fun <T> verifyNever(mock: T): T {
    return verify(mock, times(0))
}
