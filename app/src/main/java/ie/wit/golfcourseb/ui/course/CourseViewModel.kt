package ie.wit.golfcourseb.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.golfcourseb.models.GolfcourseManager
import ie.wit.golfcourseb.models.GolfcourseModel

class CourseViewModel  : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addGolfcourse(golfcourse: GolfcourseModel) {
        status.value = try {
            GolfcourseManager.create(golfcourse)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}