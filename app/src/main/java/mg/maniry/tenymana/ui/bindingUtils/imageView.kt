package mg.maniry.tenymana.ui.bindingUtils

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("srcResID")
fun ImageView.bindSrcResID(id: Int?) {
    if (id != null) {
        setImageDrawable(ResourcesCompat.getDrawable(resources, id, null))
    }
}
