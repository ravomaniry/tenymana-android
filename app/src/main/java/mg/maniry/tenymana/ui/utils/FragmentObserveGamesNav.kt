package mg.maniry.tenymana.ui.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.ui.app.Screen
import mg.maniry.tenymana.ui.gamesList.GameViewModel

fun Fragment.observeGamesNav(viewModel: GameViewModel, router: (Screen?) -> NavDirections?) {
    viewModel.screen.observe(viewLifecycleOwner, Observer {
        val direction = router(it)
        if (direction != null && viewModel.shouldNavigate) {
            viewModel.shouldNavigate = false
            findNavController().navigate(direction)
        }
    })
}
