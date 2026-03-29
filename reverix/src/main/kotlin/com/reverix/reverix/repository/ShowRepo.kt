package com.reverix.reverix.repos

import com.reverix.reverix.entities.Show
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ShowRepository : JpaRepository<Show, Long> {
    fun findByMovieId(movieId: Long): List<Show>
    fun findByTheatreId(theatreId: Long): List<Show>
    fun findByMovieIdAndShowTimeAfter(
        movieId: Long,
        showTime: LocalDateTime
    ): List<Show>
}