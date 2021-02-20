package mg.maniry.tenymana.ui.journeyDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyDownloadItemBinding
import mg.maniry.tenymana.repositories.models.ApiJourneyListItem

class JourneyAdapter(
    private val onDownload: (ApiJourneyListItem) -> Unit
) : ListAdapter<DisplayJourneyItem, JourneyAdapter.ViewHolder>(DiffUtilCb()) {
    var items: List<ApiJourneyListItem> = emptyList()
        set(value) {
            field = value
            updateList()
        }

    var downloaded: Set<String> = emptySet()
        set(value) {
            field = value
            updateList()
        }

    private fun updateList() {
        submitList(
            items.map { DisplayJourneyItem(it, downloaded.contains(it.id), onDownload) }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCb : DiffUtil.ItemCallback<DisplayJourneyItem>() {
        override fun areItemsTheSame(
            oldItem: DisplayJourneyItem,
            newItem: DisplayJourneyItem
        ): Boolean {
            return oldItem.value.id == newItem.value.id
        }

        override fun areContentsTheSame(
            oldItem: DisplayJourneyItem,
            newItem: DisplayJourneyItem
        ): Boolean {
            return oldItem.isDownloaded == newItem.isDownloaded
        }
    }

    class ViewHolder private constructor(
        private val binding: JourneyDownloadItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DisplayJourneyItem) {
            binding.value = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: JourneyDownloadItemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.journey_download_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

}

class DisplayJourneyItem(
    val value: ApiJourneyListItem,
    val isDownloaded: Boolean,
    private val onDownload: (ApiJourneyListItem) -> Unit
) {
    fun onClick() = onDownload(value)
}
