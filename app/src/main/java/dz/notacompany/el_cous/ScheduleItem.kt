package dz.notacompany.el_cous

data class ScheduleItem(
    val itemID : String,
    val itemOrder : Int,
    val scheduleDepartureTime : String,
    val scheduleArrivalTime : String,
    val delays : Int,
    var userHasReported : Boolean
)