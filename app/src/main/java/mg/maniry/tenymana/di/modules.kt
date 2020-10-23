package mg.maniry.tenymana.di

import android.content.Context
import mg.maniry.tenymana.api.FileApiImpl
import mg.maniry.tenymana.api.FsHelperImpl
import mg.maniry.tenymana.repositories.*
import mg.maniry.tenymana.ui.app.SharedViewModels
import mg.maniry.tenymana.utils.Random
import mg.maniry.tenymana.utils.RandomImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single<UserRepo> { UserRepoImpl() }
    single<GameRepo> { GameRepoImpl(fs(androidContext())) }
    single { SharedViewModels() }
    single<Random> { RandomImpl() }
    single<BibleRepo> { BibleRepoImpl(fs(androidContext())) }
}

private fun fs(context: Context) = FsHelperImpl(FileApiImpl(context))
