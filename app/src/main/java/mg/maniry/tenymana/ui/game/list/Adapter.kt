package mg.maniry.tenymana.ui.game.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GamesListScreenItemBinding
import mg.maniry.tenymana.repositories.models.Session

class SessionAdapter(
    clickHandler: (Session) -> Unit
) : RecyclerView.Adapter<SessionViewHolder>() {
    private val onClick = OnSessionClick(clickHandler)
    var data: List<Session> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        return SessionViewHolder.from(parent)
    }

    override fun onBindViewHolder(vh: SessionViewHolder, pos: Int) {
        vh.bind(data[pos], onClick)
    }
}

class SessionViewHolder private constructor(
    private val binding: GamesListScreenItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Session, onClick: OnSessionClick) {
        binding.session = item
        binding.onClick = onClick
        binding.sessionProgress.progress = item.percentage
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SessionViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: GamesListScreenItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.games_list_screen_item, parent, false)
            return SessionViewHolder(binding)
        }
    }
}

class OnSessionClick(
    val handler: (session: Session) -> Unit
) {
    fun onClick(session: Session) = handler(session)
}

private val Session.percentage: Int
    get() {
        return when (journey.size) {
            0 -> 0
            else -> (100 * progress.size / journey.size)
        }
    }
