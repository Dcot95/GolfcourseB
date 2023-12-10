package ie.wit.golfcourseb.ui.played

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.golfcourseb.firebase.FirebaseDBManager
import ie.wit.golfcourseb.models.GolfcourseModel
import timber.log.Timber
import java.lang.Exception

class PlayedViewModel : ViewModel() {

    private val golfcoursesList =
        MutableLiveData<List<GolfcourseModel>>()

    val observableGolfcoursesList: LiveData<List<GolfcourseModel>>
        get() = golfcoursesList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            //GolfcourseManager.findAll(liveFirebaseUser.value?.email!!, golfcoursesList)
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!,golfcoursesList)
            Timber.i("Played Load Success : ${golfcoursesList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Played Load Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            //GolfcourseManager.delete(userid,id)
            FirebaseDBManager.delete(userid,id)
            Timber.i("Played Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Played Delete Error : $e.message")
        }
    }
}

