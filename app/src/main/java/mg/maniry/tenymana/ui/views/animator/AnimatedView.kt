package mg.maniry.tenymana.ui.views.animator

interface AnimatedView {
    var animator: Animator?

    fun onFrame()

    fun onTick(t: Long): Boolean

    fun onDispose() {
        animator?.forget(this)
    }
}
