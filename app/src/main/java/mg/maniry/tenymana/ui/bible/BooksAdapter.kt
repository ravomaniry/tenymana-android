package mg.maniry.tenymana.ui.bible

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.BibleBookNameItemBinding
import mg.maniry.tenymana.ui.bindingUtils.bindBackgroundColor
import mg.maniry.tenymana.ui.bindingUtils.setTextColorResID

class BooksAdapter(
    onSelect: (Int) -> Unit
) : ListAdapter<DisplayBook, BooksAdapter.ViewHolder>(DiffUtilCb()) {
    private val onClick = OnBookClick(onSelect)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class ViewHolder private constructor(
        private val binding: BibleBookNameItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: DisplayBook, onClick: OnBookClick) {
            binding.book = book
            binding.onClick = onClick
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: BibleBookNameItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.bible_book_name_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    class DiffUtilCb : DiffUtil.ItemCallback<DisplayBook>() {
        override fun areItemsTheSame(oldItem: DisplayBook, newItem: DisplayBook): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: DisplayBook, newItem: DisplayBook): Boolean {
            return oldItem == newItem
        }
    }
}

data class DisplayBook(val name: String, val index: Int, val active: Boolean)

class OnBookClick(private val handler: (Int) -> Unit) {
    fun onClick(index: Int) = handler(index)
}

@BindingAdapter("bibleBookActive")
fun TextView.bindBibleBookActive(value: Boolean?) {
    if (value == true) {
        setTextColorResID(R.color.light)
        bindBackgroundColor(R.color.blue)
    } else {
        setTextColorResID(R.color.dark)
        bindBackgroundColor(R.color.light)
    }
}
