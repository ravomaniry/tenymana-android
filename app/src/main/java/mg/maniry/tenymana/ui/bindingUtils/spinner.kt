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

@BindingAdapter("selectedString")
fun Spinner.bindSelected(value: String?) {
    val selected = selectedItem
    if (value != null && value != selected) {
        try {
            for (i in 0 until adapter.count) {
                val item = adapter.getItem(i)
                if (item == value) {
                    setSelection(i)
                    return
                }
            }
        } catch (e: Exception) {
            println("Failed to select: ${e.message}")
        }
    }
}
