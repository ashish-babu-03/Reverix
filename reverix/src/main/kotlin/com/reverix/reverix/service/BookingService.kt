package com.reverix.reverix.service

import com.reverix.reverix.dto.BookingConfirmationResponse
import com.reverix.reverix.dto.LockSeatsResponse
import com.reverix.reverix.dto.SeatInfo
import com.reverix.reverix.dto.SeatRecommendationResponse
import com.reverix.reverix.entity.*
import com.reverix.reverix.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val showRepository: ShowRepository,
    private val seatRepository: SeatRepository,
    private val userRepository: UserRepository
) {

    // Step 1 — Lock seats temporarily (10 min window)
    @Transactional
    fun lockSeats(
        userId: Long,
        showId: Long,
        seatIds: List<Long>
    ): LockSeatsResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        val show = showRepository.findById(showId)
            .orElseThrow { RuntimeException("Show not found") }

        // CinePrime check — if show is prime early access
        // only PRIME_USER can book
        if (show.isPrimeEarlyAccess && user.role != Role.PRIME_USER) {
            throw RuntimeException(
                "This show is currently only available for Reverix Prime members. " +
                        "Upgrade to Prime for early access!"
            )
        }

        val seats = seatRepository.findAllById(seatIds)

        // Check all seats are available
        val unavailable = seats.filter {
            it.status != SeatStatus.AVAILABLE
        }
        if (unavailable.isNotEmpty()) {
            val nums = unavailable.joinToString(", ") { it.seatNumber }
            throw RuntimeException("Seats already taken: $nums")
        }

        // Lock all seats for 10 minutes
        val lockUntil = LocalDateTime.now().plusMinutes(10)
        seats.forEach { seat ->
            seat.status = SeatStatus.LOCKED
            seat.lockedByUserId = userId
            seat.lockedUntil = lockUntil
        }
        seatRepository.saveAll(seats)

        val totalAmount = show.price.multiply(
            BigDecimal(seats.size)
        )

        return LockSeatsResponse(
            showId = showId,
            lockedSeatIds = seatIds,
            lockedSeatNumbers = seats.map { it.seatNumber },
            totalAmount = totalAmount,
            lockExpiresAt = lockUntil,
            message = "Seats locked for 10 minutes. Complete booking before expiry!"
        )
    }

    // Step 2 — Confirm booking after payment
    @Transactional
    fun confirmBooking(
        userId: Long,
        showId: Long,
        seatIds: List<Long>
    ): BookingConfirmationResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        val show = showRepository.findById(showId)
            .orElseThrow { RuntimeException("Show not found") }

        val seats = seatRepository.findAllById(seatIds)

        // Verify seats are still locked by this user
        val invalidSeats = seats.filter {
            it.status != SeatStatus.LOCKED ||
                    it.lockedByUserId != userId ||
                    it.lockedUntil?.isBefore(LocalDateTime.now()) == true
        }
        if (invalidSeats.isNotEmpty()) {
            throw RuntimeException(
                "Seat lock expired or invalid. Please select seats again."
            )
        }

        // Mark seats as booked
        seats.forEach { seat ->
            seat.status = SeatStatus.BOOKED
            seat.lockedByUserId = null
            seat.lockedUntil = null
        }
        seatRepository.saveAll(seats)

        // Update available seats count on show
        show.availableSeats -= seats.size
        showRepository.save(show)

        // Create booking record
        val totalAmount = show.price.multiply(BigDecimal(seats.size))
        val booking = Booking(
            user = user,
            show = show,
            seatIds = seatIds.joinToString(","),
            totalAmount = totalAmount,
            status = BookingStatus.CONFIRMED,
            isPrimeBooking = user.role == Role.PRIME_USER
        )
        val saved = bookingRepository.save(booking)

        return BookingConfirmationResponse(
            bookingId = saved.id,
            movieTitle = show.movie.title,
            theatreName = show.theatre.name,
            showTime = show.showTime,
            seatNumbers = seats.map { it.seatNumber },
            totalAmount = totalAmount,
            isPrimeBooking = booking.isPrimeBooking,
            status = "CONFIRMED",
            message = if (booking.isPrimeBooking)
                "Prime booking confirmed! Enjoy your exclusive experience!"
            else
                "Booking confirmed! Enjoy the movie!"
        )
    }

    // Cancel booking
    @Transactional
    fun cancelBooking(bookingId: Long, userId: Long): String {
        val booking = bookingRepository.findById(bookingId)
            .orElseThrow { RuntimeException("Booking not found") }

        if (booking.user.id != userId) {
            throw RuntimeException("Unauthorized to cancel this booking")
        }

        if (booking.status == BookingStatus.CANCELLED) {
            throw RuntimeException("Booking already cancelled")
        }

        // Release seats back to available
        val seatIdList = booking.seatIds
            ?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()

        val seats = seatRepository.findAllById(seatIdList)
        seats.forEach { seat ->
            seat.status = SeatStatus.AVAILABLE
            seat.lockedByUserId = null
            seat.lockedUntil = null
        }
        seatRepository.saveAll(seats)

        // Update available seats count
        val show = booking.show
        show.availableSeats += seats.size
        showRepository.save(show)

        booking.status = BookingStatus.CANCELLED
        bookingRepository.save(booking)

        return "Booking #${bookingId} cancelled successfully"
    }

    // Get user bookings
    fun getUserBookings(userId: Long): List<BookingConfirmationResponse> {
        return bookingRepository.findByUserId(userId).map { booking ->
            val seatIdList = booking.seatIds
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?: emptyList()
            val seats = seatRepository.findAllById(seatIdList)

            BookingConfirmationResponse(
                bookingId = booking.id,
                movieTitle = booking.show.movie.title,
                theatreName = booking.show.theatre.name,
                showTime = booking.show.showTime,
                seatNumbers = seats.map { it.seatNumber },
                totalAmount = booking.totalAmount,
                isPrimeBooking = booking.isPrimeBooking,
                status = booking.status.name,
                message = "Booking ${booking.status.name.lowercase()}"
            )
        }
    }

    // Get recommended seats based on group type
    fun getRecommendedSeats(
        showId: Long,
        groupType: String,
        groupSize: Int
    ): SeatRecommendationResponse {
        val zone = when (groupType.lowercase()) {
            "friends" -> SeatZone.MIDDLE
            "family" -> SeatZone.BACK
            "couple", "partner" -> SeatZone.MIDDLE
            "solo" -> SeatZone.MIDDLE
            else -> SeatZone.MIDDLE
        }

        val availableInZone = seatRepository
            .findByShowIdAndZoneAndStatus(
                showId, zone, SeatStatus.AVAILABLE
            )

        // Try to find consecutive seats
        val recommended = findConsecutiveSeats(
            availableInZone, groupSize
        )

        return SeatRecommendationResponse(
            recommendedZone = zone.name,
            recommendedSeats = recommended.map { seat ->
                SeatInfo(
                    id = seat.id,
                    seatNumber = seat.seatNumber,
                    zone = seat.zone.name,
                    status = seat.status.name
                )
            },
            message = when (groupType.lowercase()) {
                "friends" -> "Middle zone recommended — best for group fun!"
                "family" -> "Back zone recommended — spacious and comfortable!"
                "couple", "partner" -> "Middle zone recommended — perfect view!"
                "solo" -> "Middle zone recommended — best screen experience!"
                else -> "Here are the best available seats!"
            }
        )
    }

    private fun findConsecutiveSeats(
        seats: List<Seat>,
        count: Int
    ): List<Seat> {
        if (seats.size < count) return seats.take(count)
        // Sort by seat number and try to find consecutive block
        val sorted = seats.sortedBy { it.seatNumber }
        for (i in 0..sorted.size - count) {
            val block = sorted.subList(i, i + count)
            return block
        }
        return sorted.take(count)
    }
}