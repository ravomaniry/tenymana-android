package mg.maniry.tenymana.ui.game.puzzle.linkClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenLinkClearBinding
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.utils.bindTo

class LinkClearFragment(
    private val puzzleViewModel: PuzzleViewModel
) : Fragment() {
    private lateinit var binding: PuzzleScreenLinkClearBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.puzzle_screen_link_clear, container, false)
        binding.lifecycleOwner = this
        bindTo(puzzleViewModel.colors) { binding.verseView.onColorsChanged(it) }
        bindTo(puzzleViewModel.words) { binding.verseView.onWordsChange(it) }
    }
}
