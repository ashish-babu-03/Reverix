package com.reverix.reverix.controller

import com.reverix.reverix.dto.*
import com.reverix.reverix.service.BookingService
import com.reverix.reverix.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookings")
class BookingController(
    private val bookingService: BookingService,
    private val userService: UserService
) {

    @PostMapping("/lock-seats")
    fun lockSeats(
        @RequestBody request: LockSeatsRequest,
        authentication: Authentication
    ): ResponseEntity<LockSeatsResponse> {
        val userId = userService.getUserIdByEmail(authentication.name)
        return ResponseEntity.ok(
            bookingService.lockSeats(userId, request.showId, request.seatIds)
        )
    }

    @PostMapping("/confirm")
    fun confirmBooking(
        @RequestBody request: ConfirmBookingRequest,
        authentication: Authentication
    ): ResponseEntity<BookingConfirmationResponse> {
        val userId = userService.getUserIdByEmail(authentication.name)
        return ResponseEntity.ok(
            bookingService.confirmBooking(userId, request.showId, request.seatIds)
        )
    }

    @DeleteMapping("/{bookingId}")
    fun cancelBooking(
        @PathVariable bookingId: Long,
        authentication: Authentication
    ): ResponseEntity<String> {
        val userId = userService.getUserIdByEmail(authentication.name)
        return ResponseEntity.ok(
            bookingService.cancelBooking(bookingId, userId)
        )
    }

    @GetMapping("/my-bookings")
    fun getMyBookings(
        authentication: Authentication
    ): ResponseEntity<List<BookingConfirmationResponse>> {
        val userId = userService.getUserIdByEmail(authentication.name)
        return ResponseEntity.ok(
            bookingService.getUserBookings(userId)
        )
    }

    @GetMapping("/recommend-seats")
    fun recommendSeats(
        @RequestParam showId: Long,
        @RequestParam groupType: String,
        @RequestParam groupSize: Int
    ): ResponseEntity<SeatRecommendationResponse> {
        return ResponseEntity.ok(
            bookingService.getRecommendedSeats(showId, groupType, groupSize)
        )
    }
}