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
import mg.maniry.tenymana.ui.app.AnimatorWrapper
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class LinkClearFragment : Fragment() {
    private lateinit var binding: PuzzleScreenLinkClearBinding
    private lateinit var viewModel: LinkClearViewModel
    private val drawingSettings =
        DrawingSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        initVerseView()
        initCharsGridView()
        initCharsGridInput()
        initGridHighlightVew()
        initBonusViews()
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
            bindTo(viewModel.colors, this::onColorsChanged)
            bindTo(viewModel.words, this::onWordsChange)
        }
    }

    private fun initCharsGridView() {
        binding.charsGridBg.apply {
            onSettingsChanged(drawingSettings)
            onVisibleHChanged(LinkClearPuzzle.gridSize)
            bindTo(viewModel.grid, this::onGridChanged)
            bindTo(viewModel.colors, this::onColorsChanged)
        }
    }

    private fun initCharsGridInput() {
        binding.charsGridInput.apply {
            onSettingsChanged(drawingSettings)
            onVisibleHChanged(LinkClearPuzzle.gridSize)
            bindTo(viewModel.propose, this::onPropose)
            bindTo(viewModel.grid, this::onGridChanged)
            bindTo(viewModel.colors, this::onColorsChanged)
        }
    }

    private fun initGridHighlightVew() {
        val anim: AnimatorWrapper by inject()
        binding.gridHighlightView.apply {
            animator = anim.value
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.grid, this::onGridChanged)
            bindTo(viewModel.animDuration, this::onAnimDurationChanged)
            bindTo(viewModel.colors, this::onColorsChanged)
            bindTo(viewModel.highlighted, this::onValue)
        }
    }

    private fun initBonusViews() {
        bindTo(viewModel.colors, binding.puzzleBonusOneBtn::onColorsChanged)
    }

    private fun observeReRender() {
        viewModel.invalidate.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.charsGridBg.update()
                binding.verseView.invalidate()
                binding.gridHighlightView.update()
                viewModel.onUpdateDone()
            }
        })
    }
}
