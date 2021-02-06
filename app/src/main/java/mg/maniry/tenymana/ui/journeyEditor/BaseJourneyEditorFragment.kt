package mg.maniry.tenymana.ui.journeyEditor

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.utils.KDispatchers
import org.koin.android.ext.android.inject

abstract class BaseJourneyEditorFragment : Fragment() {
    protected lateinit var viewModel: JourneyEditorVM

    protected fun initViewModel() {
        val userRepo: UserRepo by inject()
        val gameRepo: GameRepo by inject()
        val bibleRepo: BibleRepo by inject()
        val kDispatchers: KDispatchers by inject()
        val factory = JourneyEditorVM.factory(userRepo, gameRepo, bibleRepo, kDispatchers)
        viewModel = ViewModelProvider(requireActivity(), factory).get(JourneyEditorVM::class.java)
    }
}
