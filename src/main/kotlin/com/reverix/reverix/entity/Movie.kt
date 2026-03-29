package com.reverix.reverix.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "movies")
data class Movie(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tmdb_id", unique = true, length = 50)
    var tmdbId: String? = null,

    @Column(nullable = false, length = 200)
    var title: String = "",

    @Column(length = 200)
    var genre: String? = null,

    @Column(length = 50)
    var language: String? = null,

    @Column(name = "mood_tags", length = 255)
    var moodTags: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "poster_url", length = 500)
    var posterUrl: String? = null,

    @Column
    var rating: Double? = null,

    @Column(name = "release_date")
    var releaseDate: LocalDate? = null,

    @Column(name = "is_rentable")
    var isRentable: Boolean = false,

    @Column(name = "rent_price", precision = 8, scale = 2)
    var rentPrice: BigDecimal? = null
)