package mg.maniry.tenymana.ui.views.spinner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter

class SpinnerStringArray : AppCompatSpinner {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    private var list: List<String>? = null
    private var value: String? = null

    fun setList(list: List<String>) {
        this.list = mutableListOf("").apply { addAll(list) }
        val nextAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1,
            this.list!!
        )
        nextAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter = nextAdapter
        if (value != null) {
            setSelection(this.list!!.indexOf(value!!))
        }
    }

    fun select(value: String) {
        if (value != this.value) {
            this.value = value
            val i = list?.indexOf(value)
            if (i != null && i >= 0) {
                setSelection(i)
            }
        }
    }

    fun onChange(callback: (Int) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 > 0) {
                    callback(p2 - 1)
                    value = list?.get(p2)
                }
            }
        }
    }
}

@BindingAdapter("list")
fun SpinnerStringArray.bindList(value: List<String>?) {
    if (value != null) {
        setList(value)
    }
}

@BindingAdapter("onChange")
fun SpinnerStringArray.bindOnchange(cb: ((Int) -> Unit)?) {
    if (cb != null) {
        onChange(cb)
    }
}

@BindingAdapter("value")
fun SpinnerStringArray.bindValue(value: String?) {
    if (value != null) {
        select(value)
    }
}
