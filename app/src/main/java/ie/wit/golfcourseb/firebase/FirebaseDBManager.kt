package ie.wit.golfcourseb.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.models.GolfcourseStore
import timber.log.Timber

object FirebaseDBManager : GolfcourseStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(golfcoursesList: MutableLiveData<List<GolfcourseModel>>) {
        database.child("golfcourses")
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
                    database.child("golfcourses")
                        .removeEventListener(this)

                    golfcoursesList.value = localList
                }
            })
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

    fun updateImageRef(userid: String,imageUri: String) {

        val userGolfcoures = database.child("user-golfcourses").child(userid)
        val allGolfcourses = database.child("golfcourses")

        userGolfcoures.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all golfcourses that match 'it'
                        val golfcourse = it.getValue(GolfcourseModel::class.java)
                        allGolfcourses.child(golfcourse!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }
}