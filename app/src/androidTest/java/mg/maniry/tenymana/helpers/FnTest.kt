package mg.maniry.tenymana.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FnTest {
    @Test
    fun fn() {
        val fn = Fn<Int>()
        // empty
        assertThat(fn.called).isFalse()
        assertThat(fn.calledTimes).isEqualTo(0)
        assertThat(fn.calls).isEmpty()
        // mock once + mock forever
        fn.mockReturnValueOnce(10).mockReturnValue(5)
        assertThat(fn()).isEqualTo(10)
        assertThat(fn()).isEqualTo(5)
        assertThat(fn()).isEqualTo(5)
        assertThat(fn.called).isTrue()
        assertThat(fn.notCalled).isFalse()
        assertThat(fn.calledTimes).isEqualTo(3)
        assertThat(fn.calls).isEqualTo(mutableListOf(listOf<Any>(), listOf(), listOf()))
        assertThat(fn.calledWith()).isTrue()
        assertThat(fn.calledWith(1)).isFalse()
        // reset + mock impl
        fn.reset()
        assertThat(fn.calls).isEmpty()
        fn.mockImplementationOnce { it[0] as Int + it[1] as Int }
            .mockImplementation { it[0] as Int * 2 }
        assertThat(fn(1, 2)).isEqualTo(3)
        assertThat(fn(3)).isEqualTo(6)
        assertThat(fn.calledWith(1, 2)).isTrue()
        assertThat(fn.calledWith(3)).isTrue()
        // clear
        fn.clear()
        assertThat(fn.calledWith(3)).isFalse()
    }

    @Test
    fun defaultFn() {
        val fn = Fn<Int>()
        assertThat(fn()).isNull()
    }
}