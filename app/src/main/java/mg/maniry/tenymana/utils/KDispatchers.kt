package mg.maniry.tenymana.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay as kDelay

interface KDispatchers {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    suspend fun delay(millis: Long)
}

object RealDispatchers : KDispatchers {
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
    override val io = Dispatchers.IO

    override suspend fun delay(millis: Long) {
        kDelay(millis)
    }
}
