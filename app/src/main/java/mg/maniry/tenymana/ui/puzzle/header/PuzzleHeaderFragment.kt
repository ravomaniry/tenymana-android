package mg.maniry.tenymana.ui.puzzle.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHeaderBinding
import mg.maniry.tenymana.ui.puzzle.PuzzleViewModel

class PuzzleHeaderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity()).get(PuzzleViewModel::class.java)
        val binding: PuzzleScreenHeaderBinding = DataBindingUtil
            .inflate(inflater, R.layout.puzzle_screen_header, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
}
