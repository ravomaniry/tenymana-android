package mg.maniry.tenymana.ui.bindingUtils

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("backgroundColorID")
fun View.bindBackgroundColor(resID: Int?) {
    if (resID == null || resID == 0) {
        setBackgroundColor(Color.TRANSPARENT)
    } else {
        setBackgroundColor(ContextCompat.getColor(context, resID))
    }
}

@BindingAdapter("isVisible")
fun View.bindIsVisible(value: Boolean?) {
    visibility = if (value == false) View.GONE else View.VISIBLE
}

@BindingAdapter("backgroundDrawableID")
fun View.bindBackgroundDrawable(resID: Int?) {
    background = when (resID) {
        null -> null
        else -> ContextCompat.getDrawable(context, resID)
    }
}
