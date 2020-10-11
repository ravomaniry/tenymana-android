package mg.maniry.tenymana.utils

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class InsideOutIteratorTest {
    @Test
    fun singleEl() {
        val it = InsideOutIterator(listOf(10), 0)
        assertThat(it.hasNext).isTrue()
        assertThat(it.next()).isEqualTo(10)
        assertThat(it.hasNext).isFalse()
    }

    @Test
    fun iterate() {
        val it = InsideOutIterator(listOf(0, 1, 2, 3, 4), 3)
        val values = listOf(3, 2, 4, 1, 0)
        for (v in values) {
            assertThat(it.hasNext).isTrue()
            assertThat(it.next()).isEqualTo(v)
        }
        assertThat(it.hasNext).isFalse()
    }

    @Test
    fun random() {
        val random: Random = mock {
            on { int(0, 2) } doReturn 2
        }
        val it = InsideOutIterator.random(listOf(0, 1, 2), random)
        val values = listOf(2, 1, 0)
        for (v in values) {
            assertThat(it.hasNext).isTrue()
            assertThat(it.next()).isEqualTo(v)
        }
        assertThat(it.hasNext).isFalse()
    }
}
