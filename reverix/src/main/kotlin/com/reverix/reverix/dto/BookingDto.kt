package com.reverix.reverix.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class LockSeatsRequest(
    val showId: Long,
    val seatIds: List<Long>
)

data class LockSeatsResponse(
    val showId: Long,
    val lockedSeatIds: List<Long>,
    val lockedSeatNumbers: List<String>,
    val totalAmount: BigDecimal,
    val lockExpiresAt: LocalDateTime,
    val message: String
)

data class ConfirmBookingRequest(
    val showId: Long,
    val seatIds: List<Long>
)

data class BookingConfirmationResponse(
    val bookingId: Long,
    val movieTitle: String,
    val theatreName: String,
    val showTime: LocalDateTime,
    val seatNumbers: List<String>,
    val totalAmount: BigDecimal,
    val isPrimeBooking: Boolean,
    val status: String,
    val message: String
)

data class SeatRecommendationResponse(
    val recommendedZone: String,
    val recommendedSeats: List<SeatInfo>,
    val message: String
)

data class SeatInfo(
    val id: Long,
    val seatNumber: String,
    val zone: String,
    val status: String
)