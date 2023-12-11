package hotel.lv2

import java.time.LocalDate

data class RsvHistory (
    val people: People,
    val resvMoney: Int,
    var checkIn: LocalDate,
    var checkOut: LocalDate,
    val resvRoom: Int,
)
