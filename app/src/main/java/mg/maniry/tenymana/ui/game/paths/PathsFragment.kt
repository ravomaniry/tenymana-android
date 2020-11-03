package mg.maniry.tenymana.ui.game.paths

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathsScreenBinding
import mg.maniry.tenymana.ui.game.GameViewModel

class PathsFragment(
    private val gameViewModel: GameViewModel
) : Fragment() {
    private lateinit var viewModel: PathsViewModel
    private lateinit var binding: PathsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        return binding.root
    }

    private fun initViewModel() {
        val factory = PathsViewModelFactory(gameViewModel)
        viewModel = ViewModelProvider(this, factory).get(PathsViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.paths_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}
