package mg.maniry.tenymana.ui.game.paths

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class PathsFragment : Fragment() {
    private lateinit var viewModel: PathsViewModel
    private lateinit var binding: PathsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        initGrid()
        return binding.root
    }

    private fun initViewModel() {
        val viewModels: SharedViewModels by inject()
        val factory = PathsViewModelFactory(viewModels.game)
        viewModel = ViewModelProvider(this, factory).get(PathsViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.paths_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initGrid() {
        val pathsAdapter = PathAdapter(viewModel.verseClickHandler)
        binding.pathsGrid.apply {
            adapter = pathsAdapter
            (layoutManager as GridLayoutManager).spanCount = 8
        }
        bindTo(viewModel.activePath) { pathsAdapter.path = it }
        bindTo(viewModel.activeScores) { pathsAdapter.scores = it }
    }
}
