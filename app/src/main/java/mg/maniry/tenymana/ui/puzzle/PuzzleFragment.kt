package mg.maniry.tenymana.ui.puzzle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PuzzleScreenBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.ui.puzzle.header.PuzzleHeaderFragment
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
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.puzzle_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        val vms: SharedViewModels by inject()
        val fct = PuzzleViewModelFactory(vms.game)
        viewModel = ViewModelProvider(this, fct).get(PuzzleViewModel::class.java)
    }

    private fun initHeader() {
        mountChild(PuzzleHeaderFragment(), R.id.journeyHeaderPlaceHolder)
    }
}
