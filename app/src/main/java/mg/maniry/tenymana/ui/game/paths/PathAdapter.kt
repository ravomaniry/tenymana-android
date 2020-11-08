package mg.maniry.tenymana.ui.game.paths

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenVerseItemBinding
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.repositories.models.Path

class PathAdapter(
    verseClickHandler: (Int) -> Unit
) : ListAdapter<PathVerseItem, PathAdapter.ViewHolder>(DiffUtilCb()) {
    private val onClick = OnVerseClick(verseClickHandler)

    var scores: List<Score>? = null

    var path: Path? = null
        set(value) {
            field = value
            submitList(value?.toVerseItems(scores))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class DiffUtilCb : DiffUtil.ItemCallback<PathVerseItem>() {
        override fun areItemsTheSame(oldItem: PathVerseItem, newItem: PathVerseItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PathVerseItem, newItem: PathVerseItem): Boolean {
            return oldItem.value == newItem.value &&
                    oldItem.star0 == newItem.star0 &&
                    oldItem.star1 == newItem.star1 &&
                    oldItem.star2 == newItem.star2
        }
    }

    class ViewHolder private constructor(
        private val binding: PathsScreenVerseItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PathVerseItem, onClick: OnVerseClick) {
            binding.item = item
            binding.onClick = onClick
            binding.background = when (item.score) {
                0 -> R.drawable.bg_paths_screen_item_new
                else -> R.drawable.bg_paths_screen_item_done
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: PathsScreenVerseItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.paths_screen_verse_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }
}

class OnVerseClick(
    val handler: (index: Int) -> Unit
) {
    fun onClick(index: Int) = handler(index)
}

data class PathVerseItem(
    val index: Int,
    val value: String,
    val score: Int,
    val star0: Int,
    val star1: Int,
    val star2: Int
)

private fun Path.toVerseItems(scores: List<Score>?): List<PathVerseItem> {
    val list = mutableListOf<PathVerseItem>()
    for (v in start..end) {
        val index = v - start
        val score = scores?.getOrNull(index) ?: Score.ZERO
        val stars = score.stars
        list.add(
            PathVerseItem(
                index,
                v.toString(),
                score.value,
                stars.toStarID(1),
                stars.toStarID(2),
                stars.toStarID(3)
            )
        )
    }
    return list
}

private fun Int.toStarID(min: Int): Int {
    return when {
        this >= min -> R.drawable.ic_star_rate
        else -> R.drawable.ic_star_rate_white
    }
}
