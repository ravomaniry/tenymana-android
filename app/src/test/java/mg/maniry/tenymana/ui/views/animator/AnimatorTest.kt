package mg.maniry.tenymana.ui.views.animator

import android.animation.ValueAnimator
import com.nhaarman.mockitokotlin2.*
import mg.maniry.tenymana.utils.verifyNever
import mg.maniry.tenymana.utils.verifyOnce
import org.junit.Test

class AnimatorTest {
    @Test
    fun animator() {
        lateinit var listener: ValueAnimator.AnimatorUpdateListener
        var isRunning = false
        val animation: ValueAnimator = mock {
            on { addUpdateListener(any()) } doAnswer {
                listener = it.arguments[0] as ValueAnimator.AnimatorUpdateListener; Unit
            }
            on { this.isRunning } doAnswer { isRunning }
            on { this.cancel() } doAnswer { isRunning = false; Unit }
            on { this.start() } doAnswer { isRunning = true; Unit }
        }
        val animator = Animator(animation)
        verifyNever(animation).start()
        // Register starts the animation
        var onView1Tick = false
        var onView2Tick = false
        val view1: AnimatedView = mock {
            on { onTick(any()) } doAnswer { onView1Tick }
        }
        val view2: AnimatedView = mock {
            on { onTick(any()) } doAnswer { onView2Tick }
        }
        // Add a view starts the animation if it's not running
        animator.register(view1)
        verifyOnce(animation).start()
        verifyNever(animation).cancel()
        listener.onAnimationUpdate(animation)
        verifyOnce(view1).onTick(any())
        verifyNever(view1).reRender()
        // Add another: no more start + ticks return true
        onView1Tick = true
        onView2Tick = true
        clearInvocations(animation, view1)
        animator.register(view2)
        verifyZeroInteractions(animation)
        listener.onAnimationUpdate(animation)
        verifyOnce(view1).onTick(any())
        verifyOnce(view1).reRender()
        verifyOnce(view2).onTick(any())
        verifyOnce(view2).reRender()
        // Forget view2
        clearInvocations(animation, view1, view2)
        animator.forget(view2)
        verifyZeroInteractions(animation)
        listener.onAnimationUpdate(animation)
        verifyZeroInteractions(view2)
        verifyOnce(view1).onTick(any())
        verifyOnce(view1).reRender()
        // Forget view1 stops animation
        animator.forget(view1)
        verifyOnce(animation).cancel()
        // Concurrent modif + views are empty: starts animation
        clearInvocations(animation)
        val view3: AnimatedView = mock()
        whenever(view3.onTick(any())).doAnswer { animator.forget(view3);false }
        animator.register(view3)
        animator.register(view1)
        verifyOnce(animation).start()
        listener.onAnimationUpdate(animation)
        verifyOnce(animation).cancel()
    }
}
