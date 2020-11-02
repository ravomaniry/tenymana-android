package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.repositories.BibleRepo
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.KDispatchers
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RealDispatchers
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun setupTestKoin(testModules: Module = createTestModule()) {
    try {
        startMockedKoin(testModules)
    } catch (e: IllegalStateException) {
        stopKoin()
        startMockedKoin(testModules)
    }
}

fun createTestModule(): Module {
    return module {
        single<UserRepo> { UserRepoMock() }
        single<GameRepo> { GameRepoMock() }
        single { SharedViewModels() }
        single<Random> { RandomMock() }
        single<BibleRepo> { BibleRepoMock() }
        single<KDispatchers> { RealDispatchers }
    }
}

private fun startMockedKoin(testModules: Module) {
    startKoin {
        modules(testModules)
    }
}
