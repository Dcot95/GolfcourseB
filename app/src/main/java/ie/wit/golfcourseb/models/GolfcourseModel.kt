package ie.wit.golfcourseb.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GolfcourseModel(var id: Long = 0,
                         val paymentmethod: String = "N/A",
                         val amount: Int = 0) : Parcelable