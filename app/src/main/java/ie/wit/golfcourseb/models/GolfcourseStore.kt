package ie.wit.golfcourseb.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface GolfcourseStore {
    fun findAll(golfcoursesList:
                MutableLiveData<List<GolfcourseModel>>
    )
    fun findAll(userid:String,
                golfcoursesList:
                MutableLiveData<List<GolfcourseModel>>
    )
    fun findById(userid:String, golfcourseid: String,
                 golfcourse: MutableLiveData<GolfcourseModel>
    )
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, golfcourse: GolfcourseModel)
    fun delete(userid:String, golfcourseid: String)
    fun update(userid:String, golfcourseid: String, golfcourse: GolfcourseModel)
}

