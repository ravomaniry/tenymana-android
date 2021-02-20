package mg.maniry.tenymana.di

import android.content.Context
import mg.maniry.tenymana.api.FileApiImpl
import mg.maniry.tenymana.api.FsHelperImpl
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilder
import mg.maniry.tenymana.gameLogic.shared.puzzleBuilder.PuzzleBuilderImpl
import mg.maniry.tenymana.repositories.*
import mg.maniry.tenymana.repositories.network.ApiClient
import mg.maniry.tenymana.ui.app.AnimatorWrapper
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single<UserRepo> { UserRepoImpl(fs(androidContext()), RandomImpl(), DateTimeUtilImpl()) }
    single<GameRepo> { GameRepoImpl(fs(androidContext()), ApiClient(androidContext())) }
    single { SharedViewModels() }
    single<PuzzleBuilder> { PuzzleBuilderImpl(RandomImpl()) }
    single<BibleRepo> { BibleRepoImpl(fs(androidContext())) }
    single<KDispatchers> { RealDispatchers }
    single { AnimatorWrapper() }
}

private fun fs(context: Context) = FsHelperImpl(FileApiImpl(context), AssetsWrapperImpl(context))
