package mg.maniry.tenymana.ui.bindingUtils

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("intValue")
fun TextView.bindIntValue(value: Int?) {
    text = value?.toString() ?: ""
}

@BindingAdapter("truncatedText")
fun TextView.bindTruncatedText(value: String?) {
    if (value != null) {
        text = if (value.length > 100) "${value.substring(0, 100)} ..." else value
    }
}
