package ie.wit.golfcourseb.main

import android.app.Application
import timber.log.Timber

class GolfcourseBApp : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("GolfcourseB Application Started")
    }
}