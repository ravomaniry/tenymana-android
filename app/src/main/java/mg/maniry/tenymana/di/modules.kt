package mg.maniry.tenymana.di

import mg.maniry.tenymana.api.FileApiImpl
import mg.maniry.tenymana.api.FsHelperImpl
import mg.maniry.tenymana.repositories.GameRepo
import mg.maniry.tenymana.repositories.GameRepoImpl
import mg.maniry.tenymana.repositories.UserRepo
import mg.maniry.tenymana.repositories.UserRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single<UserRepo> { UserRepoImpl() }
    single<GameRepo> { GameRepoImpl(FsHelperImpl(FileApiImpl(androidContext()))) }
}
