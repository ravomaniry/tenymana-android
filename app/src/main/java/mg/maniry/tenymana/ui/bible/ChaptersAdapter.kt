package mg.maniry.tenymana.ui.bible

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.BibleChapterItemBinding

class ChaptersAdapter(
    onSelect: (Int) -> Unit
) : ListAdapter<Int, ChaptersAdapter.ViewHolder>(DiffUtilCb()) {
    private val onClick = OnChapterSelect(onSelect)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class DiffUtilCb : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder private constructor(
        private val binding: BibleChapterItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: Int, onClick: OnChapterSelect) {
            binding.index = value.toString()
            binding.onClick = onClick
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: BibleChapterItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.bible_chapter_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }
}

class OnChapterSelect(
    private val handler: (Int) -> Unit
) {
    fun onClick(index: String) = handler(index.toInt())
}
