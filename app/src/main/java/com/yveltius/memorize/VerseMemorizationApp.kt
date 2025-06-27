package com.yveltius.memorize

import android.app.Application
import com.yveltius.memorize.viewmodels.VersesListViewModel
import com.yveltius.versememorization.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class VerseMemorizationApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VerseMemorizationApp)
            modules(*KoinModules.modules, viewModelModule)
        }
    }

    private val viewModelModule = module {
        factory { VersesListViewModel() }
    }
}