package mg.maniry.tenymana.ui.game.paths

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenVerseItemBinding
import mg.maniry.tenymana.gameLogic.models.Score
import mg.maniry.tenymana.repositories.models.Path

class PathAdapter(
    verseClickHandler: (Int) -> Unit
) : RecyclerView.Adapter<PathViewHolder>() {
    private val onClick = OnVerseClick(verseClickHandler)

    var scores: List<Score>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var path: Path? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return when (path) {
            null -> 0
            else -> path!!.end - path!!.start + 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        return PathViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        if (path != null) {
            val score = if (scores != null && scores!!.size > position) scores!![position] else null
            holder.bind(position, path!!.start + position, score, onClick)
        }
    }
}

class PathViewHolder private constructor(
    private val binding: PathsScreenVerseItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(index: Int, verse: Int, score: Score?, onClick: OnVerseClick) {
        binding.index = index
        binding.verse = verse.toString()
        binding.score = score ?: Score.ZERO
        binding.onClick = onClick
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): PathViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: PathsScreenVerseItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.paths_screen_verse_item, parent, false)
            return PathViewHolder(binding)
        }
    }
}

class OnVerseClick(
    val handler: (index: Int) -> Unit
) {
    fun onClick(index: Int) = handler(index)
}
