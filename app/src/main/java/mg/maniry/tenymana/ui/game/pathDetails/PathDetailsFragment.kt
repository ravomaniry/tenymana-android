package mg.maniry.tenymana.ui.game.pathDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.PathDetailsScreenBinding
import mg.maniry.tenymana.gameLogic.shared.session.SessionPosition
import mg.maniry.tenymana.ui.game.GameViewModel

class PathDetailsFragment(
    private val gameViewModel: GameViewModel,
    position: SessionPosition?
) : Fragment() {
    private lateinit var binding: PathDetailsScreenBinding
    private val path = gameViewModel.session.value?.journey?.paths?.get(position?.pathIndex ?: 0)

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
        binding = DataBindingUtil.inflate(inflater, R.layout.path_details_screen, container, false)
        binding.lifecycleOwner = this
        binding.path = path
        binding.gameViewModel = gameViewModel
    }
}
