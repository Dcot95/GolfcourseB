package ie.wit.golfcourseb.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.golfcourseb.firebase.FirebaseDBManager
import ie.wit.golfcourseb.models.GolfcourseModel

class CourseViewModel  : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addGolfcourse(firebaseUser: MutableLiveData<FirebaseUser>,
                    golfcourse: GolfcourseModel) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser,golfcourse)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}