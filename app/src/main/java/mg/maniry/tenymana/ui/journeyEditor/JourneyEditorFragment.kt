package mg.maniry.tenymana.ui.journeyEditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import mg.maniry.tenymana.R
import mg.maniry.tenymana.utils.bindTo
import mg.maniry.tenymana.utils.mountChild

class JourneyEditorFragment : BaseJourneyEditorFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewModel()
        observeNav()
        return inflater.inflate(R.layout.journey_editor, container, false)
    }

    private fun observeNav() {
        bindTo(viewModel.route) {
            val child = when (it) {
                JourneyEditorVM.Route.Summary -> SummaryEditorFragment()
                else -> PathEditorFragment()
            }
            mountChild(child, R.id.journeyEditorPH)
        }
        bindTo(viewModel.goToHome) {
            if (it) {
                findNavController().popBackStack()
                viewModel.goToHome.postValue(false)
            }
        }
    }
}
