package mg.maniry.tenymana.ui.game.journey.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.JourneyScreenHeaderBinding
import mg.maniry.tenymana.ui.game.journey.JourneyViewModel

class JourneyHeaderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity()).get(JourneyViewModel::class.java)
        val binding: JourneyScreenHeaderBinding = DataBindingUtil
            .inflate(inflater, R.layout.journey_screen_header, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
}
