package mg.maniry.tenymana.utils

import androidx.fragment.app.Fragment

fun Fragment.mountChild(child: Fragment, placeHolderID: Int) {
    childFragmentManager.beginTransaction().replace(placeHolderID, child).commit()
}
