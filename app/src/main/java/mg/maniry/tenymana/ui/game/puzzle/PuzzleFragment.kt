package mg.maniry.tenymana.ui.game.puzzle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenBinding
import mg.maniry.tenymana.gameLogic.anagram.AnagramPuzzle
import mg.maniry.tenymana.gameLogic.hiddenWords.HiddenWordsPuzzle
import mg.maniry.tenymana.gameLogic.linkClear.LinkClearPuzzle
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.game.puzzle.anagram.AnagramFragment
import mg.maniry.tenymana.ui.game.puzzle.header.PuzzleHeaderFragment
import mg.maniry.tenymana.ui.game.puzzle.hiddenWords.HiddenWordsFragment
import mg.maniry.tenymana.ui.game.puzzle.linkClear.LinkClearFragment
import mg.maniry.tenymana.ui.game.puzzle.loader.PuzzleLoaderFragment
import mg.maniry.tenymana.utils.mountChild
import org.koin.android.ext.android.inject

class PuzzleFragment : Fragment() {
    private lateinit var binding: PuzzleScreenBinding
    private lateinit var viewModel: PuzzleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        initHeader()
        observeRoute()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.puzzle_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        val viewModels: SharedViewModels by inject()
        val fct = PuzzleViewModel.factory(viewModels.game)
        viewModel = ViewModelProvider(requireActivity(), fct).get(PuzzleViewModel::class.java)
        viewModels.puzzle = viewModel
    }

    private fun initHeader() {
        mountChild(PuzzleHeaderFragment(), R.id.puzzleHeaderPlaceHolder)
    }

    private fun observeRoute() {
        viewModel.puzzle.observe(viewLifecycleOwner, Observer {
            val body = when (it) {
                is LinkClearPuzzle -> LinkClearFragment()
                is HiddenWordsPuzzle -> HiddenWordsFragment()
                is AnagramPuzzle -> AnagramFragment()
                else -> PuzzleLoaderFragment()
            }
            mountChild(body, R.id.puzzleBodyPlaceHolder)
        })
    }
}
