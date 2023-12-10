package ie.wit.golfcourseb.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.golfcourseb.firebase.FirebaseDBManager
import ie.wit.golfcourseb.models.GolfcourseModel
import timber.log.Timber

class GolfcourseDetailViewModel : ViewModel() {
    private val golfcourse = MutableLiveData<GolfcourseModel>()

    var observableGolfcourse: LiveData<GolfcourseModel>
        get() = golfcourse
        set(value) {golfcourse.value = value.value}

    fun getGolfcourse(userid:String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, golfcourse)
            Timber.i("Detail getGolfcourse() Success : ${
                golfcourse.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getGolfcourse() Error : $e.message")
        }
    }

    fun updateGolfcourse(userid:String, id: String,golfcourse: GolfcourseModel) {
        try {
            FirebaseDBManager.update(userid, id, golfcourse)
            Timber.i("Detail update() Success : $golfcourse")
        }
        catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}