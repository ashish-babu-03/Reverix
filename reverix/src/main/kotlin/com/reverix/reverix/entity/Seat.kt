package com.reverix.reverix.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "seats")
data class Seat(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    var show: Show = Show(),

    @Column(name = "seat_number", nullable = false, length = 10)
    var seatNumber: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var zone: SeatZone = SeatZone.MIDDLE,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SeatStatus = SeatStatus.AVAILABLE,

    @Column(name = "locked_by_user_id")
    var lockedByUserId: Long? = null,

    @Column(name = "locked_until")
    var lockedUntil: LocalDateTime? = null
)

enum class SeatZone {
    FRONT, MIDDLE, BACK
}

enum class SeatStatus {
    AVAILABLE, LOCKED, BOOKED
}