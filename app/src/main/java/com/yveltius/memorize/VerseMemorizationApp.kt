package com.yveltius.memorize

import android.app.Application
import com.yveltius.memorize.features.addverse.viewmodels.AddVerseViewModel
import com.yveltius.memorize.features.practice.viewmodels.choosenextword.ChooseNextWordViewModel
import com.yveltius.memorize.features.main.viewmodels.MainViewModel
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
        factory { MainViewModel() }
        factory { AddVerseViewModel() }
        factory { ChooseNextWordViewModel() }
    }
}