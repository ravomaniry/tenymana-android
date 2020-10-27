package mg.maniry.tenymana.utils

data class TestRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    companion object {
        fun xywh(x: Float, y: Float, w: Float, h: Float): TestRect {
            return TestRect(x, y, x + w, y + h)
        }
    }
}

data class TestTextShape(
    val value: String,
    val x: Float,
    val y: Float
)

class TestMotionEvent(
    val action: Int,
    val x: Float,
    val y: Float
)

data class TestLine(
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float
)
