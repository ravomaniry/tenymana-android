package mg.maniry.tenymana.ui.bindingUtils

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter

@BindingAdapter("onChange")
fun Spinner.onChange(callback: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            callback(p2)
        }
    }
}

@BindingAdapter("stringsList")
fun Spinner.bindList(value: List<String>?) {
    if (value != null) {
        val simpleListItem = android.R.layout.simple_list_item_1
        adapter = ArrayAdapter<String>(context, simpleListItem, value).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}
