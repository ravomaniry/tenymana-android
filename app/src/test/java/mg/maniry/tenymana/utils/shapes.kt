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

        fun fromMock(arguments: Array<Any?>): TestRect {
            return TestRect(
                arguments[0] as Float,
                arguments[1] as Float,
                arguments[2] as Float,
                arguments[3] as Float
            )
        }
    }
}

data class TestTextShape(
    val value: String,
    val x: Float,
    val y: Float
) {
    companion object {
        fun fromMock(arguments: Array<Any?>): TestTextShape {
            return TestTextShape(
                arguments[0] as String,
                arguments[1] as Float,
                arguments[2] as Float
            )
        }
    }
}

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
