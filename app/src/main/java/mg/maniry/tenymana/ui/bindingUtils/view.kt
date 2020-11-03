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