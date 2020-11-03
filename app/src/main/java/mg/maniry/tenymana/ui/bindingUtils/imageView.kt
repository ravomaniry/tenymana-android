package mg.maniry.tenymana.ui.bindingUtils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("srcResID")
fun ImageView.bindSrcResID(id: Int?) {
    if (id != null) {
        setImageDrawable(resources.getDrawable(id))
    }
}
