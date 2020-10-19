package mg.maniry.tenymana.ui.game.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyScreenBinding
import mg.maniry.tenymana.ui.game.journey.header.JourneyHeaderFragment
import mg.maniry.tenymana.utils.mountChild

class JourneyFragment : Fragment() {
    private lateinit var binding: JourneyScreenBinding
    private lateinit var viewModel: JourneyViewModel

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
        binding = DataBindingUtil.inflate(inflater, R.layout.journey_screen, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(JourneyViewModel::class.java)
    }

    private fun initHeader() {
        mountChild(JourneyHeaderFragment(), R.id.journeyHeaderPlaceHolder)
    }
}
