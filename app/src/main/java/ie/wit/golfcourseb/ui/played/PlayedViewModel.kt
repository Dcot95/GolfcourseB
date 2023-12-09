package ie.wit.golfcourseb.ui.played

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.golfcourseb.models.GolfcourseManager
import ie.wit.golfcourseb.models.GolfcourseModel

class PlayedViewModel : ViewModel() {

    private val golfcoursesList = MutableLiveData<List<GolfcourseModel>>()

    val observableGolfcoursesList: LiveData<List<GolfcourseModel>>
        get() = golfcoursesList

    init {
        load()
    }

    fun load() {
        golfcoursesList.value = GolfcourseManager.findAll()
    }
}