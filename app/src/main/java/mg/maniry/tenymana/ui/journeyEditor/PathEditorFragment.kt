package mg.maniry.tenymana.ui.journeyEditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyEditorPathBinding

class PathEditorFragment : BaseJourneyEditorFragment() {
    private lateinit var binding: JourneyEditorPathBinding

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

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.journey_editor_path, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
