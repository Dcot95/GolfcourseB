package ie.wit.golfcourseb.models


import timber.log.Timber

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class GolfcourseMemStore : GolfcourseStore {

    val golfcourses = ArrayList<GolfcourseModel>()

    override fun findAll(): List<GolfcourseModel> {
        return golfcourses
    }

    override fun findById(id:Long) : GolfcourseModel? {
        val foundGolfcourse: GolfcourseModel? = golfcourses.find { it.id == id }
        return foundGolfcourse
    }

    override fun create(golfcourse: GolfcourseModel) {
        golfcourse.id = getId()
        golfcourses.add(golfcourse)
        logAll()
    }

    fun logAll() {
        Timber.v("** Golfcourses List **")
        golfcourses.forEach { Timber.v("Course ${it}") }
    }
}