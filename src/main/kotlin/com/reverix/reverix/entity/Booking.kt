package com.reverix.reverix.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "bookings")
data class Booking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    var show: Show = Show(),

    @Column(name = "seat_ids", length = 255)
    var seatIds: String? = null,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookingStatus = BookingStatus.PENDING,

    @Column(name = "is_prime_booking")
    var isPrimeBooking: Boolean = false,

    @Column(name = "booked_at")
    val bookedAt: LocalDateTime = LocalDateTime.now()
)

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}