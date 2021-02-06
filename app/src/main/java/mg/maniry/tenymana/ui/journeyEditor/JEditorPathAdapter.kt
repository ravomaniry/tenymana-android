package mg.maniry.tenymana.ui.journeyEditor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyEditorPathPreviewItemBinding
import mg.maniry.tenymana.repositories.models.Path

class JEditorPathAdapter(
    private val onSelect: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<JEditorPathAdapter.ViewHolder>() {
    private var list: List<JEditorPathWrapper> = emptyList()

    fun submitList(paths: List<Path>) {
        list = paths.mapIndexed { index, path ->
            JEditorPathWrapper(path, index, onSelect, onDelete)
        }
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ViewHolder private constructor(
        private val binding: JourneyEditorPathPreviewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JEditorPathWrapper) {
            binding.path = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return ViewHolder(
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.journey_editor_path_preview_item,
                        parent,
                        false
                    )
                )
            }
        }
    }
}

class JEditorPathWrapper(
    val path: Path,
    private val index: Int,
    private val selectHandler: (Int) -> Unit,
    private val deleteHandler: (Int) -> Unit
) {
    val verseRef = "${path.book} ${path.chapter}: ${path.start} - ${path.end}"

    fun onSelect() {
        selectHandler(index)
    }

    fun onDelete() {
        deleteHandler(index)
    }
}
