package dz.notacompany.el_cous

data class ScheduleItem(
    val itemID : String,
    val itemOrder : Int,
    val scheduleDepartureTime : String,
    val scheduleArrivalTime : String
)