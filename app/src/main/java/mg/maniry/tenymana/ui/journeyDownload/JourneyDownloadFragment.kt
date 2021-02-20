package mg.maniry.tenymana.ui.journeyDownload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyDownloadScreenBinding
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.bindTo
import org.koin.android.ext.android.inject

class JourneyDownloadFragment : Fragment() {
    private lateinit var viewModel: JourneyDownloadVM
    private lateinit var binding: JourneyDownloadScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        initBinding(inflater, container)
        initRecyclerView()
        return binding.root
    }

    private fun initViewModel() {
        val userRepo: UserRepo by inject()
        val gameRepo: GameRepo by inject()
        val kDispatchers: KDispatchers by inject()
        val factory = JourneyDownloadVM.factory(userRepo, gameRepo, kDispatchers)
        viewModel = ViewModelProvider(this, factory).get(JourneyDownloadVM::class.java)
        viewModel.refreshList()
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.journey_download_screen,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.journeyDldCloseBtn.setOnClickListener {
            findNavController().popBackStack()
            viewModel.onClose()
        }
    }

    private fun initRecyclerView() {
        val rView = binding.journeysList
        val adapter = JourneyAdapter(viewModel::download)
        bindTo(viewModel.list) { adapter.items = it }
        bindTo(viewModel.downloaded) { adapter.downloaded = it }
        rView.adapter = adapter
    }
}
