package com.reverix.reverix.repos

import com.reverix.reverix.entities.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUserId(userId: Long): List<Booking>
    fun findByShowId(showId: Long): List<Booking>
}