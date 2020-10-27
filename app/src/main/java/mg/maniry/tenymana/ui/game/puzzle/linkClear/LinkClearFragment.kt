package mg.maniry.tenymana.ui.game.puzzle.linkClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenLinkClearBinding
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.ui.game.puzzle.PuzzleViewModel
import mg.maniry.tenymana.ui.game.puzzle.views.DrawingSettings
import mg.maniry.tenymana.utils.bindTo

class LinkClearFragment(
    private val puzzleViewModel: PuzzleViewModel
) : Fragment() {
    private lateinit var binding: PuzzleScreenLinkClearBinding
    private val drawingSettings = DrawingSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initVerseView()
        initCharsGridView()
        initCharsGridInput()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.puzzle_screen_link_clear, container, false)
        binding.lifecycleOwner = this
    }

    private fun initVerseView() {
        val view = binding.verseView
        view.onSettingsChanged(drawingSettings)
        bindTo(puzzleViewModel.colors, view::onColorsChanged)
        bindTo(puzzleViewModel.puzzle) { view.onWordsChange(it?.verse?.words) }
    }

    private fun initCharsGridView() {
        val view = binding.charsGrid
        view.onSettingsChanged(drawingSettings)
        view.onVisibleHChanged(LinkClearPuzzle.visibleH)
        bindTo(puzzleViewModel.colors, view::onColorsChanged)
        bindTo(puzzleViewModel.puzzle) {
            if (it != null) {
                view.onGridChanged((it as LinkClearPuzzle).grid)
            }
        }
    }

    private fun initCharsGridInput() {
        val view = binding.charsGridInput
        view.onSettingsChanged(drawingSettings)
        view.onPropose(puzzleViewModel::propose)
        bindTo(puzzleViewModel.colors, view::onColorsChanged)
        bindTo(puzzleViewModel.puzzle) {
            if (it != null) {
                view.onGridChanged((it as LinkClearPuzzle).grid)
            }
        }
    }
}
