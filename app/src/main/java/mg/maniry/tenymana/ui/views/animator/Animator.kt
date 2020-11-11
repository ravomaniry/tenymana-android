package mg.maniry.tenymana.ui.views.animator

import android.animation.ValueAnimator
import java.util.*

class Animator(
    private val animation: ValueAnimator
) {
    private val views = mutableSetOf<AnimatedView>()

    fun register(view: AnimatedView) {
        views.add(view)
        if (views.size == 1) {
            animation.startIfNotRunning()
        }
    }

    fun forget(view: AnimatedView) {
        views.remove(view)
        if (views.isEmpty()) {
            animation.stopIfRunning()
        }
    }

    private fun onTick() {
        val t = Date().time
        for (v in views) {
            val invalidate = v.onTick(t)
            if (invalidate) {
                v.reRender()
            }
        }
    }

    init {
        animation.apply {
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { onTick() }
        }
    }
}

private fun ValueAnimator.startIfNotRunning() {
    if (!isRunning) {
        start()
    }
}

private fun ValueAnimator.stopIfRunning() {
    if (isRunning) {
        cancel()
    }
}
