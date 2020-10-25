package mg.maniry.tenymana.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun Fragment.mountChild(child: Fragment, placeHolderID: Int) {
    childFragmentManager.beginTransaction().replace(placeHolderID, child).commit()
}

fun <T> Fragment.bindTo(value: LiveData<T>, receiver: (T) -> Unit) {
    value.observe(viewLifecycleOwner, Observer(receiver))
}
