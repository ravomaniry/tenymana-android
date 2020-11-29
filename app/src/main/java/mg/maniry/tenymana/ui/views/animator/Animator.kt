package mg.maniry.tenymana.ui.views.animator

import android.animation.ValueAnimator
import java.util.*

class Animator(
    private val animation: ValueAnimator
) {
    private val views = mutableSetOf<AnimatedView>()
    private val toForget = mutableSetOf<AnimatedView>()
    private var frameDone = true

    fun register(view: AnimatedView) {
        views.add(view)
        if (views.size == 1) {
            animation.startIfNotRunning()
        }
    }

    fun forget(view: AnimatedView) {
        toForget.add(view)
        applyForget()
    }

    private fun applyForget() {
        if (toForget.isNotEmpty() && frameDone) {
            for (v in toForget) {
                views.remove(v)
            }
            toForget.removeAll { true }
            if (views.isEmpty()) {
                animation.stopIfRunning()
            }
        }
    }

    private fun onTick() {
        frameDone = false
        val t = Date().time
        for (v in views) {
            val invalidate = v.onTick(t)
            if (invalidate) {
                v.onFrame()
            }
        }
        frameDone = true
        applyForget()
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
