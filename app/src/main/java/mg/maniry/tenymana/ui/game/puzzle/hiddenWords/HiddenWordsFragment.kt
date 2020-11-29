package mg.maniry.tenymana.ui.game.puzzle.hiddenWords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.KDispatchers
import org.koin.android.ext.android.inject

class HiddenWordsFragment : Fragment() {
    private lateinit var viewModel: HiddenWordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        return null
    }

    private fun initViewModel() {
        val sharedVM: SharedViewModels by inject()
        val kDispatchers: KDispatchers by inject()
        val factory = HiddenWordsViewModel.factory(sharedVM.puzzle, kDispatchers)
        viewModel = ViewModelProvider(requireActivity(), factory)
            .get(HiddenWordsViewModel::class.java)
    }
}
