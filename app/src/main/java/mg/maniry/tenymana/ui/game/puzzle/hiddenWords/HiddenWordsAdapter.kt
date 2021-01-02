package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHiddenWordsGroupItemBinding
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup

class HiddenWordsAdapter(
    private val viewModel: HiddenWordsViewModel
) : ListAdapter<HiddenWordsGroup, HiddenWordsAdapter.ViewHolder>(DiffUtilCb()) {
    var width: Int = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var groups: List<HiddenWordsGroup>? = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = groups?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(width, position, viewModel)
    }

    class ViewHolder private constructor(
        private val binding: PuzzleScreenHiddenWordsGroupItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: PuzzleScreenHiddenWordsGroupItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.puzzle_screen_hidden_words_group_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }

        fun bind(width: Int, index: Int, viewModel: HiddenWordsViewModel) {
            binding.itemContainer.parentW = width
            binding.index = index
            binding.viewModel = viewModel
        }
    }

    class DiffUtilCb : DiffUtil.ItemCallback<HiddenWordsGroup>() {
        override fun areItemsTheSame(old: HiddenWordsGroup, newItem: HiddenWordsGroup): Boolean {
            return old == newItem
        }

        override fun areContentsTheSame(old: HiddenWordsGroup, curr: HiddenWordsGroup): Boolean {
            return old.hidden == curr.hidden
        }
    }
}
