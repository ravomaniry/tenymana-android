package mg.maniry.tenymana.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CartesianTest {
    @Test
    fun upward() {
        testEq(1, 1, 3, 2, 0.0, 0.5)
        testEq(1, 1, 3, 2, 2.0, 1.5)
        testEq(1, 1, 3, 2, 5.0, 3.0)
        testEq(1, 1, 3, 2, -1.0, 0.0)
    }

    @Test
    fun downward() {
        testEq(1, 3, 2, 1, 2.5, 0.0)
        testEq(1, 3, 2, 1, 3.0, -1.0)
    }

    private fun testEq(x0: Int, y0: Int, x1: Int, y1: Int, x: Double, y: Double) {
        val eq = Cartesian(x0, y0, x1, y1)
        assertThat(eq.fx(x)).isEqualTo(y)
        assertThat(eq.fy(y)).isEqualTo(x)
    }
}
