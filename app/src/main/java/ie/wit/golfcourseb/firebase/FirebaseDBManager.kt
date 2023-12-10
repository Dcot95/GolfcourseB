package ie.wit.golfcourseb.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.models.GolfcourseStore
import timber.log.Timber

object FirebaseDBManager : GolfcourseStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(golfcoursesList: MutableLiveData<List<GolfcourseModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, golfcoursesList: MutableLiveData<List<GolfcourseModel>>) {

        database.child("user-golfcourses").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Golfcourse error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<GolfcourseModel>()
                    val children = snapshot.children
                    children.forEach {
                        val golfcourse = it.getValue(GolfcourseModel::class.java)
                        localList.add(golfcourse!!)
                    }
                    database.child("user-golfcourses").child(userid)
                        .removeEventListener(this)

                    golfcoursesList.value = localList
                }
            })
    }

    override fun findById(userid: String, golfcourseid: String, golfcourse: MutableLiveData<GolfcourseModel>) {

        database.child("user-golfcourses").child(userid)
            .child(golfcourseid).get().addOnSuccessListener {
                golfcourse.value = it.getValue(GolfcourseModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, golfcourse: GolfcourseModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("golfcourses").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        golfcourse.uid = key
        val golfcourseValues = golfcourse.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/golfcourses/$key"] = golfcourseValues
        childAdd["/user-golfcourses/$uid/$key"] = golfcourseValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, golfcourseid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/golfcourses/$golfcourseid"] = null
        childDelete["/user-golfcourses/$userid/$golfcourseid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, golfcourseid: String, golfcourse: GolfcourseModel) {

        val golfcourseValues = golfcourse.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["golfcourses/$golfcourseid"] = golfcourseValues
        childUpdate["user-golfcourses/$userid/$golfcourseid"] = golfcourseValues

        database.updateChildren(childUpdate)
    }
}