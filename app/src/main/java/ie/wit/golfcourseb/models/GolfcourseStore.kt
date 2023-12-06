package ie.wit.golfcourseb.models

interface GolfcourseStore {
    fun findAll() : List<GolfcourseModel>
    fun findById(id: Long) : GolfcourseModel?
    fun create(donation: GolfcourseModel)
}