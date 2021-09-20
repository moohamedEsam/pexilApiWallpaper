package android.mohamed.pexelapiproject.utility

import android.app.Application
import android.mohamed.pexelapiproject.koinModules.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
            modules(listOf(module))
        }
    }
}