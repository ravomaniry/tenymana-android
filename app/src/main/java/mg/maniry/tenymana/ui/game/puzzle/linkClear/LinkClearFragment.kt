package mg.maniry.tenymana.ui.game.puzzle.linkClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenLinkClearBinding
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.puzzle.views.DrawingSettings
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class LinkClearFragment : Fragment() {
    private lateinit var binding: PuzzleScreenLinkClearBinding
    private val drawingSettings = DrawingSettings()
    private val viewModels: SharedViewModels by inject()
    private val puzzleViewModel = viewModels.puzzle
    private val puzzle = puzzleViewModel.puzzle.value!! as LinkClearPuzzle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initVerseView()
        initCharsGridView()
        initCharsGridInput()
        observeReRender()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.puzzle_screen_link_clear, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = puzzleViewModel
    }

    private fun initVerseView() {
        binding.verseView.apply {
            onSettingsChanged(drawingSettings)
            onWordsChange(puzzle.verse.words)
            bindTo(puzzleViewModel.colors, ::onColorsChanged)
        }
    }

    private fun initCharsGridView() {
        binding.charsGrid.apply {
            onSettingsChanged(drawingSettings)
            onVisibleHChanged(LinkClearPuzzle.gridSize)
            onGridChanged(puzzle.grid)
            bindTo(puzzleViewModel.colors, ::onColorsChanged)
        }
    }

    private fun initCharsGridInput() {
        binding.charsGridInput.apply {
            onSettingsChanged(drawingSettings)
            onPropose(puzzleViewModel::propose)
            onGridChanged(puzzle.grid)
            bindTo(puzzleViewModel.colors, ::onColorsChanged)
        }
    }

    private fun observeReRender() {
        puzzleViewModel.invalidate.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.charsGrid.invalidate()
                binding.verseView.invalidate()
                puzzleViewModel.invalidate.postValue(false)
            }
        })
    }
}
