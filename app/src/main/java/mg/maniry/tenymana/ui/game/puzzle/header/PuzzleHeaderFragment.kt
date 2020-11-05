package mg.maniry.tenymana.ui.game.puzzle.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHeaderBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class PuzzleHeaderFragment : Fragment() {
    private lateinit var viewModel: PuzzleHeaderViewModel
    private lateinit var binding: PuzzleScreenHeaderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        return binding.root
    }

    private fun initViewModel() {
        val sharedVMs: SharedViewModels by inject()
        val factory = PuzzleHeaderViewModel.factory(sharedVMs.game)
        viewModel = ViewModelProvider(this, factory).get(PuzzleHeaderViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.puzzle_screen_header, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}
