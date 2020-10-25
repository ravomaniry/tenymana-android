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
