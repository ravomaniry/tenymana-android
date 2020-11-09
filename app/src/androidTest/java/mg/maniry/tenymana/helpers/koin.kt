package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.AnimatorWrapper
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.RealDispatchers
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mockito.mock

fun setupTestKoin(testModules: Module = createTestModule()) {
    try {
        startMockedKoin(testModules)
    } catch (e: IllegalStateException) {
        stopKoin()
        startMockedKoin(testModules)
    }
}

fun createTestModule(): Module {
    val userRepo = mock(UserRepo::class.java)
    val gameRepo = mock(GameRepo::class.java)
    val puzzleBuilder = mock(PuzzleBuilder::class.java)
    val bibleRepo = mock(BibleRepo::class.java)
    return module {
        single<UserRepo> { userRepo }
        single<GameRepo> { gameRepo }
        single { SharedViewModels() }
        single<PuzzleBuilder> { puzzleBuilder }
        single<BibleRepo> { bibleRepo }
        single<KDispatchers> { RealDispatchers }
        single { AnimatorWrapper() }
    }
}

private fun startMockedKoin(testModules: Module) {
    startKoin {
        modules(testModules)
    }
}
