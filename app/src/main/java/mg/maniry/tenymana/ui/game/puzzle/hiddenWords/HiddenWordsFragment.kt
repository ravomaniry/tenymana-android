package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenHiddenWordsBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.views.settings.DrawingSettings
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class HiddenWordsFragment : Fragment() {
    private lateinit var binding: PuzzleScreenHiddenWordsBinding
    private lateinit var viewModel: HiddenWordsViewModel
    private var adapter: HiddenWordsAdapter? = null
    private val drawingSettings = DrawingSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        observeScreenSize()
        initRecyclerView()
        initVerseView()
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
    }

    private fun observeScreenSize() {
        binding.hiddenWordsPuzzleBody.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val w = v.width
            if (adapter?.width != w) {
                adapter?.width = w
            }
        }
    }

    private fun initRecyclerView() {
        val view = binding.groupsList
        adapter = HiddenWordsAdapter(viewModel)
        viewModel.puzzle.observe(viewLifecycleOwner, Observer {
            adapter?.groups = it?.groups
        })
        view.adapter = adapter
    }

    private fun initVerseView() {
        binding.verseView.apply {
            onSettingsChanged(drawingSettings)
            bindTo(viewModel.colors, this::onColorsChanged)
            bindTo(viewModel.words, this::onWordsChange)
        }
    }
}
