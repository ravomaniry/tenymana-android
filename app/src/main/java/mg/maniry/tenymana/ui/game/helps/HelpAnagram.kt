package mg.maniry.tenymana.ui.game.helps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.R
import mg.maniry.tenymana.databinding.HelpAnagramBinding

class HelpAnagram : Fragment() {
    private lateinit var binding: HelpAnagramBinding

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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.help_anagram, container, false
        )
        binding.closeHelpBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
