package mg.maniry.tenymana.ui.game.puzzle.anagram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenAnagramBinding
import mg.maniry.tenymana.ui.app.AnimatorWrapper
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class AnagramFragment : Fragment() {
    private lateinit var binding: PuzzleScreenAnagramBinding
    private lateinit var viewModel: AnagramViewModel
    private val anim: AnimatorWrapper by inject()
    private val drawingSettings = DrawingSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        initVerseView()
        initInputView()
        initAnim()
        return binding.root
    }

    private fun initViewModel() {
        val sharedViewModels: SharedViewModels by inject()
        val factory = AnagramViewModel.factory(sharedViewModels.puzzle)
        viewModel = ViewModelProvider(this, factory).get(AnagramViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.puzzle_screen_anagram,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initVerseView() {
        binding.animVerseView.animator = anim.value
        binding.verseView.apply {
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.colors, this::onColorsChanged)
            bindTo(viewModel.words, this::onWordsChange)
        }
    }

    private fun initInputView() {
        binding.input.apply {
            animator = anim.value
            bindTo(viewModel.colors, this::onColorsChange)
            bindTo(viewModel.characters, this::onWordChange)
            onSelect(viewModel::onCharSelect)
        }
    }

    private fun initAnim() {
        bindTo(viewModel.animate) {
            if (it) {
                binding.input.startAnim()
                viewModel.onAnimationDone()
            }
        }
    }
}
