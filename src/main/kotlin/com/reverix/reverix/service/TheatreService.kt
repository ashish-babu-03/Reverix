package com.reverix.reverix.service

import com.reverix.reverix.entity.Theatre
import com.reverix.reverix.entity.VibeType
import com.reverix.reverix.entity.ScreenSize
import com.reverix.reverix.repository.TheatreRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(1)
class TheatreService(
    private val theatreRepository: TheatreRepository
) : ApplicationRunner {

    // Seeds dummy theatres on startup if DB is empty
    override fun run(args: ApplicationArguments) {
        if (theatreRepository.count() == 0L) {
            seedTheatres()
        }
    }

    fun getAllTheatres(): List<Theatre> =
        theatreRepository.findAll()

    fun getTheatresByCity(city: String): List<Theatre> =
        theatreRepository.findByCity(city)

    fun getTheatresByVibe(city: String, vibeType: VibeType): List<Theatre> =
        theatreRepository.findByCityAndVibeType(city, vibeType)

    fun getTheatreById(id: Long): Theatre =
        theatreRepository.findById(id)
            .orElseThrow { RuntimeException("Theatre not found") }

    fun recommendTheatres(
        city: String,
        groupType: String,
        movieGenre: String
    ): List<Theatre> {
        val vibeType = when (groupType.lowercase()) {
            "friends" -> VibeType.CELEBRATION
            "family" -> VibeType.FAMILY
            "couple", "partner" -> VibeType.DATE_NIGHT
            "solo" -> VibeType.SILENT
            else -> VibeType.SILENT
        }
        val byVibe = theatreRepository.findByCityAndVibeType(city, vibeType)
        return if (byVibe.isEmpty()) {
            theatreRepository.findByCity(city)
                .sortedByDescending { it.avgRating }
        } else {
            byVibe.sortedByDescending { it.avgRating }
        }
    }

    private fun seedTheatres() {
        val theatres = listOf(
            Theatre(
                name = "PVR IMAX Chennai",
                location = "Express Avenue Mall, Royapettah",
                city = "Chennai",
                vibeType = VibeType.PREMIERE,
                screenSize = ScreenSize.IMAX,
                avgRating = 4.5,
                totalReviews = 1200
            ),
            Theatre(
                name = "SPI Sathyam Cinemas",
                location = "Royapettah High Road",
                city = "Chennai",
                vibeType = VibeType.SILENT,
                screenSize = ScreenSize.LARGE,
                avgRating = 4.3,
                totalReviews = 980
            ),
            Theatre(
                name = "Rohini Silver Screens",
                location = "Kasi Theatre Complex, Ashok Nagar",
                city = "Chennai",
                vibeType = VibeType.CELEBRATION,
                screenSize = ScreenSize.LARGE,
                avgRating = 4.7,
                totalReviews = 2100
            ),
            Theatre(
                name = "AGS Cinemas",
                location = "OMR, Perungudi",
                city = "Chennai",
                vibeType = VibeType.FAMILY,
                screenSize = ScreenSize.STANDARD,
                avgRating = 4.1,
                totalReviews = 650
            ),
            Theatre(
                name = "Escape Cinemas",
                location = "Express Avenue",
                city = "Chennai",
                vibeType = VibeType.DATE_NIGHT,
                screenSize = ScreenSize.STANDARD,
                avgRating = 4.2,
                totalReviews = 430
            ),
            Theatre(
                name = "PVR Phoenix",
                location = "Phoenix MarketCity, Velachery",
                city = "Chennai",
                vibeType = VibeType.CELEBRATION,
                screenSize = ScreenSize.LARGE,
                avgRating = 4.4,
                totalReviews = 1800
            ),
            Theatre(
                name = "INOX Mahindra",
                location = "Mahindra World City",
                city = "Chennai",
                vibeType = VibeType.FAMILY,
                screenSize = ScreenSize.STANDARD,
                avgRating = 4.0,
                totalReviews = 320
            ),
            Theatre(
                name = "Kasi Theatre",
                location = "Ashok Nagar",
                city = "Chennai",
                vibeType = VibeType.CELEBRATION,
                screenSize = ScreenSize.LARGE,
                avgRating = 4.8,
                totalReviews = 3200
            )
        )
        theatreRepository.saveAll(theatres)
        println("Seeded ${theatres.size} theatres successfully!")
    }
}