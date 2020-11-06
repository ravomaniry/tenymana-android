package mg.maniry.tenymana.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
object TestDispatchers : KDispatchers {
    override val main = TestCoroutineDispatcher()
    override val default = TestCoroutineDispatcher()
    override val io = TestCoroutineDispatcher()

    override suspend fun delay(millis: Long) {}
}
