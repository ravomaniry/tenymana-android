package mg.maniry.tenymana.ui.game.solution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.GameSolutionScreenBinding
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.ui.game.GameViewModel
import mg.maniry.tenymana.utils.bindTo

class SolutionFragment(
    private val gameViewModel: GameViewModel,
    private val bibleRepo: BibleRepo
) : Fragment() {
    private lateinit var viewModel: SolutionViewModel
    private lateinit var binding: GameSolutionScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        initBinding(inflater, container)
        initRView()
        return binding.root
    }

    private fun initViewModel() {
        val factory = SolutionViewModel.factory(gameViewModel, bibleRepo)
        viewModel = ViewModelProvider(this, factory).get(SolutionViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_solution_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initRView() {
        val adapter = VerseAdapter()
        binding.solutionScreenVersesList.adapter = adapter
        bindTo(viewModel.verses) { adapter.verses = it }
    }
}
