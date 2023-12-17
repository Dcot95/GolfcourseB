package ie.wit.golfcourseb.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class GolfcourseModel(
    var uid: String? = "",
    var typeOfCourse: String = "N/A",
    var amount: Int = 0,
    var message: String = "Homer for President!",
    var rating: Float = 0.0f,
    var profilepic: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var email: String? = "joe@bloggs.com")
    : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "typeOfCourse" to typeOfCourse,
            "amount" to amount,
            "message" to message,
            "rating" to rating,
            "profilepic" to profilepic,
            "latitude" to latitude,
            "longitude" to longitude,
            "email" to email
        )
    }
}


