package mg.maniry.tenymana.ui.bible

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.BibleScreenBinding
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.ui.game.solution.VerseAdapter
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
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
        initBooksList()
        initChaptersList()
        initVersesView()
        observeNav()
        return binding.root
    }

    private fun initViewModel() {
        val bibleRepo: BibleRepo by inject()
        val kDispatchers: KDispatchers by inject()
        val factory = BibleViewModel.factory(bibleRepo, kDispatchers)
        viewModel = ViewModelProvider(this, factory).get(BibleViewModel::class.java)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bible_screen, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun initBooksList() {
        val rView = binding.bibleBooksList
        val adapter = BooksAdapter(viewModel::onBookSelect)
        rView.adapter = adapter
        val obs = Observer<Any?> {
            val active = viewModel.book.value
            val list = viewModel.bookNames.value?.mapIndexed { i, name ->
                DisplayBook(name, i, name == active)
            }
            adapter.submitList(list)
        }
        viewModel.bookNames.observe(viewLifecycleOwner, obs)
        viewModel.book.observe(viewLifecycleOwner, obs)
    }

    private fun initChaptersList() {
        val rView = binding.bibleChaptersList
        val adapter = ChaptersAdapter(viewModel::onChapterSelect)
        rView.adapter = adapter
        bindTo(viewModel.chapters, adapter::submitList)
    }

    private fun initVersesView() {
        val rView = binding.bibleVerses
        val adapter = VerseAdapter()
        rView.adapter = adapter
        bindTo(viewModel.verses) { adapter.verses = it }
    }

    fun observeNav() {
        viewModel.shouldClose.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.shouldClose.postValue(false)
                findNavController().popBackStack()
            }
        })
    }
}
