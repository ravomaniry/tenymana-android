package mg.maniry.tenymana.ui.views.hiddenWords

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import mg.maniry.tenymana.gameLogic.models.Character

class HiddenWordsInputView : BaseHiddenWordsView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    override val control = HiddenWordsInputViewControl()

    fun onSelect(handler: SelectHandler) {
        control.onSelect(handler)
    }

    fun onGroupIndex(index: Int) {
        control.groupIndex = index
    }
}

@BindingAdapter("chars")
fun HiddenWordsInputView.bindChars(chars: List<Character?>?) {
    if (chars != null) {
        onWordChange(chars)
    }
}

@BindingAdapter("groupIndex")
fun HiddenWordsInputView.bindGroupIndex(index: Int?) {
    if (index != null) {
        onGroupIndex(index)
    }
}

@BindingAdapter("onSelect")
fun HiddenWordsInputView.bindOnSelect(handler: SelectHandler?) {
    if (handler != null) {
        onSelect(handler)
    }
}
