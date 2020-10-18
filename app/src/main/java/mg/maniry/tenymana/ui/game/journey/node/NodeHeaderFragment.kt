package mg.maniry.tenymana.ui.game.journey.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.NodeHeaderFragmentBinding

class NodeHeaderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity()).get(NodeViewModel::class.java)
        val binding: NodeHeaderFragmentBinding = DataBindingUtil
            .inflate(inflater, R.layout.node_header_fragment, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
}
