package com.reverix.reverix.repos

import com.reverix.reverix.entities.Seat
import com.reverix.reverix.entities.SeatStatus
import com.reverix.reverix.entities.SeatZone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeatRepository : JpaRepository<Seat, Long> {
    fun findByShowId(showId: Long): List<Seat>
    fun findByShowIdAndStatus(showId: Long, status: SeatStatus): List<Seat>
    fun findByShowIdAndZone(showId: Long, zone: SeatZone): List<Seat>
    fun findByShowIdAndZoneAndStatus(
        showId: Long,
        zone: SeatZone,
        status: SeatStatus
    ): List<Seat>
}