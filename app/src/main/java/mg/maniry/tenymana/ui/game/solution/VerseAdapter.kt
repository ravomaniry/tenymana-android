package mg.maniry.tenymana.ui.game.solution

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GameSolutionScreenVerseItemBinding
import mg.maniry.tenymana.gameLogic.models.BibleVerse

class VerseAdapter : RecyclerView.Adapter<VerseViewHolder>() {
    var verses: List<BibleVerse>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return verses?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        return VerseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verse = verses?.get(position)
        if (verse != null) {
            holder.bind(verse)
        }
    }
}

class VerseViewHolder private constructor(
    private val binding: GameSolutionScreenVerseItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(verse: BibleVerse) {
        binding.verse = verse
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VerseViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: GameSolutionScreenVerseItemBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_solution_screen_verse_item,
                parent,
                false
            )
            return VerseViewHolder(binding)
        }
    }
}
