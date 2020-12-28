package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHiddenWordsGroupItemBinding
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsGroup

class HiddenWordsAdapter : RecyclerView.Adapter<HiddenWordViewHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiddenWordViewHolder {
        return HiddenWordViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HiddenWordViewHolder, position: Int) {
        holder.bind(width)
    }
}

class HiddenWordViewHolder private constructor(
    private val binding: PuzzleScreenHiddenWordsGroupItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): HiddenWordViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: PuzzleScreenHiddenWordsGroupItemBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.puzzle_screen_hidden_words_group_item,
                parent,
                false
            )
            return HiddenWordViewHolder(binding)
        }
    }

    fun bind(width: Int) {
        binding.itemContainer.parentW = width
    }
}
