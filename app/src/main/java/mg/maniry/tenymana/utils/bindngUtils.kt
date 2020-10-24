package mg.maniry.tenymana.utils

import android.graphics.Color
import android.view.View
import android.widget.TextView
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

@BindingAdapter("intValue")
fun TextView.bindIntValue(value: Int?) {
    text = value?.toString() ?: ""
}
