package mg.maniry.tenymana.helpers

import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.stubbing.OngoingStubbing

fun <T> whenever(methodCall: T): OngoingStubbing<T> {
    return Mockito.`when`(methodCall)
}

fun <T> verifyOnce(tested: T): T = verify(tested, times(1))

fun <T> verifyNever(tested: T): T = verify(tested, times(0))

fun <T> verifyTimes(tested: T, n: Int): T = verify(tested, times(n))
