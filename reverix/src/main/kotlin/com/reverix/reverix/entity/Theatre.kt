package com.reverix.reverix.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "theatres")
data class Theatre(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(nullable = false)
    var location: String = "",

    @Column(nullable = false, length = 100)
    var city: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "vibe_type", nullable = false)
    var vibeType: VibeType = VibeType.SILENT,

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_size")
    var screenSize: ScreenSize = ScreenSize.STANDARD,

    @Column(name = "avg_rating")
    var avgRating: Double = 0.0,

    @Column(name = "total_reviews")
    var totalReviews: Int = 0,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class VibeType {
    CELEBRATION, SILENT, FAMILY, DATE_NIGHT, PREMIERE
}

enum class ScreenSize {
    STANDARD, LARGE, IMAX, DRIVE_IN
}