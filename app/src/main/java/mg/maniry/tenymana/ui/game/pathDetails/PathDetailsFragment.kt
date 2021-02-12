package mg.maniry.tenymana.ui.game.pathDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathDetailsScreenBinding
import mg.maniry.tenymana.ui.app.SharedViewModels
import org.koin.android.ext.android.inject

class PathDetailsFragment : Fragment() {
    private lateinit var binding: PathDetailsScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initBinding(inflater, container)
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        val viewModels: SharedViewModels by inject()
        val gameViewModel = viewModels.game
        val position = gameViewModel.position
        val path = gameViewModel.session.value?.journey?.paths?.get(position?.pathIndex ?: 0)
        binding = DataBindingUtil.inflate(inflater, R.layout.path_details_screen, container, false)
        binding.lifecycleOwner = this
        binding.path = path
        binding.gameViewModel = gameViewModel
    }
}
