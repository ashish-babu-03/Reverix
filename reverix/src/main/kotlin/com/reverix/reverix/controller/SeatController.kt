package com.reverix.reverix.controller

import com.reverix.reverix.repository.SeatRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/seats")
class SeatController(
    private val seatRepository: SeatRepository
) {

    @GetMapping
    fun getSeatsByShow(@RequestParam showId: Long): List<SeatResponse> {
        return seatRepository.findByShowId(showId).map { seat ->
            SeatResponse(
                id = seat.id,
                seatNumber = seat.seatNumber,
                zone = seat.zone.name,
                status = seat.status.name
            )
        }
    }
}

data class SeatResponse(
    val id: Long,
    val seatNumber: String?,
    val zone: String,
    val status: String
)