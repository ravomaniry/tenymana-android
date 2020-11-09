package mg.maniry.tenymana.ui.app

import android.animation.ValueAnimator
import mg.maniry.tenymana.ui.views.animator.Animator

class AnimatorWrapper {
    val value: Animator = Animator(ValueAnimator.ofInt(0, 1))
}
