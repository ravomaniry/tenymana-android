package mg.maniry.tenymana.ui.game.puzzle.linkClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenLinkClearBinding
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.puzzle.views.CharGridBackground
import mg.maniry.tenymana.ui.game.puzzle.views.DrawingSettings
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class LinkClearFragment : Fragment() {
    private lateinit var binding: PuzzleScreenLinkClearBinding
    private lateinit var viewModel: LinkClearViewModel
    private val drawingSettings = DrawingSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        initVerseView()
        initCharsGridViews()
        initCharsGridInput()
        initGridClearedVew()
        observeReRender()
        return binding.root
    }

    private fun initViewModel() {
        val kDispatchers: KDispatchers by inject()
        val viewModels: SharedViewModels by inject()
        val factory = LinkClearViewModel.factory(viewModels.puzzle, kDispatchers)
        viewModel = ViewModelProvider(this, factory).get(LinkClearViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.puzzle_screen_link_clear, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initVerseView() {
        binding.verseView.apply {
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.colors, ::onColorsChanged)
            bindTo(viewModel.words, ::onWordsChange)
        }
    }

    private fun initCharsGridViews() {
        initCharsGridView(binding.charsGridBg)
        initCharsGridView(binding.charsGridText)
    }

    private fun initCharsGridView(view: CharGridBackground) {
        view.apply {
            onSettingsChanged(drawingSettings)
            onVisibleHChanged(LinkClearPuzzle.gridSize)
            bindTo(viewModel.grid, ::onGridChanged)
            bindTo(viewModel.colors, ::onColorsChanged)
        }
    }

    private fun initCharsGridInput() {
        binding.charsGridInput.apply {
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.propose, ::onPropose)
            bindTo(viewModel.grid, ::onGridChanged)
            bindTo(viewModel.colors, ::onColorsChanged)
        }
    }

    private fun initGridClearedVew() {
        binding.gridClearedView.apply {
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.colors, ::onColor)
            bindTo(viewModel.cleared, ::onValue)
        }
    }

    private fun observeReRender() {
        viewModel.invalidate.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.charsGridBg.invalidate()
                binding.charsGridText.invalidate()
                binding.verseView.invalidate()
                binding.gridClearedView.invalidate()
                viewModel.invalidate.postValue(false)
            }
        })
    }
}
