package mg.maniry.tenymana.utils

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify

fun <T> verifyOnce(tested: T): T = verify(tested, times(1))
fun <T> verifyNever(tested: T): T = verify(tested, times(0))
fun <T> verifyTimes(tested: T, n: Int): T = verify(tested, times(n))