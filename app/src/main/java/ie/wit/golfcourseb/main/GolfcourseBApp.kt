package ie.wit.golfcourseb.main

import android.app.Application
import ie.wit.golfcourseb.models.GolfcourseMemStore
import ie.wit.golfcourseb.models.GolfcourseStore
import timber.log.Timber

class GolfcourseBApp : Application() {

    lateinit var golfcoursesStore: GolfcourseStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        golfcoursesStore = GolfcourseMemStore()
        Timber.i("GolfcourseB Application Started")
    }
}