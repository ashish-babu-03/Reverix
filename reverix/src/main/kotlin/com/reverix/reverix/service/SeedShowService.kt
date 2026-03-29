package com.reverix.reverix.service

import com.reverix.reverix.entities.Seat
import com.reverix.reverix.entities.SeatStatus
import com.reverix.reverix.entities.SeatZone
import com.reverix.reverix.entities.Show
import com.reverix.reverix.repos.MovieRepository
import com.reverix.reverix.repos.SeatRepository
import com.reverix.reverix.repos.ShowRepository
import com.reverix.reverix.repos.TheatreRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Order(3)
class ShowSeedService(
    private val showRepository: ShowRepository,
    private val movieRepository: MovieRepository,
    private val theatreRepository: TheatreRepository,
    private val seatRepository: SeatRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (showRepository.count() > 0) {
            println("Shows already seeded, skipping.")
            return
        }

        val movies = movieRepository.findAll().take(5)
        val theatres = theatreRepository.findAll().take(4)

        if (movies.isEmpty() || theatres.isEmpty()) {
            println(" Cannot seed shows — movies or theatres are empty.")
            return
        }

        println(" Seeding shows...")

        val showTimes = listOf(
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(6),
            LocalDateTime.now().plusHours(10)
        )

        val shows = mutableListOf<Show>()

        for (movie in movies) {
            for (theatre in theatres) {
                for (time in showTimes) {
                    shows.add(
                        Show(
                            movie = movie,
                            theatre = theatre,
                            showTime = time,
                            totalSeats = 60,
                            availableSeats = 60,
                            price = BigDecimal("200.00"),
                            isPrimeEarlyAccess = false
                        )
                    )
                }
            }
        }

        val savedShows = showRepository.saveAll(shows)
        println("✅ Seeded ${savedShows.size} shows.")

        println("💺 Seeding seats...")
        val allSeats = mutableListOf<Seat>()

        for (show in savedShows) {
            val zones = listOf(
                SeatZone.FRONT to (1..20),
                SeatZone.MIDDLE to (21..40),
                SeatZone.BACK to (41..60)
            )
            for ((zone, range) in zones) {
                for (i in range) {
                    allSeats.add(
                        Seat(
                            show = show,
                            seatNumber = "${zone.name[0]}$i",
                            zone = zone,
                            status = SeatStatus.AVAILABLE,
                            lockedByUserId = null,
                            lockedUntil = null
                        )
                    )
                }
            }
        }

        seatRepository.saveAll(allSeats)
        println("✅ Seeded ${allSeats.size} seats across ${savedShows.size} shows.")
    }
}