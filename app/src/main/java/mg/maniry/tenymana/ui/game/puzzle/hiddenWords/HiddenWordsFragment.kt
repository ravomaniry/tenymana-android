package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHiddenWordsBinding
import mg.maniry.tenymana.ui.app.AnimatorWrapper
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class HiddenWordsFragment : Fragment() {
    private lateinit var binding: PuzzleScreenHiddenWordsBinding
    private lateinit var viewModel: HiddenWordsViewModel
    private val drawingSettings = DrawingSettings()
    private val anim: AnimatorWrapper by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        initVerseView()
        initHiddenWordView()
        initInputView()
        initAnim()
        viewModel.onMount()
        return binding.root
    }

    private fun initViewModel() {
        val sharedVM: SharedViewModels by inject()
        val kDispatchers: KDispatchers by inject()
        val factory = HiddenWordsViewModel.factory(sharedVM.puzzle, kDispatchers)
        viewModel = ViewModelProvider(requireActivity(), factory)
            .get(HiddenWordsViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.puzzle_screen_hidden_words,
            container,
            false
        )
        binding.lifecycleOwner = this
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

    private fun initHiddenWordView() {
        binding.hiddenWord.apply {
            animator = anim.value
            bindTo(viewModel.colors, this::onColorsChange)
            bindTo(viewModel.activeGroup) {
                onWordChange(it.hidden.chars)
                onResolved(it.resolved)
            }
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
                binding.hiddenWord.startAnim()
                binding.input.startAnim()
                viewModel.onAnimationDone()
            }
        }
    }
}
