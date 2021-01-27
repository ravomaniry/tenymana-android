package mg.maniry.tenymana.ui.bible

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.BibleScreenBinding
import mg.maniry.tenymana.repositories.BibleRepo
import org.koin.android.ext.android.inject

class BibleFragment : Fragment() {
    private lateinit var binding: BibleScreenBinding
    private lateinit var viewModel: BibleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        return binding.root
    }

    private fun initViewModel() {
        val bibleRepo: BibleRepo by inject()
        val factory = BibleViewModel.factory(bibleRepo)
        viewModel = ViewModelProvider(this, factory).get(BibleViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bible_screen, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
