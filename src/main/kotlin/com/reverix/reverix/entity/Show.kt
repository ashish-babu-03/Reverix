package com.reverix.reverix.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "shows")
data class Show(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    var movie: Movie = Movie(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    var theatre: Theatre = Theatre(),

    @Column(name = "show_time", nullable = false)
    var showTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "total_seats", nullable = false)
    var totalSeats: Int = 0,

    @Column(name = "available_seats", nullable = false)
    var availableSeats: Int = 0,

    @Column(nullable = false, precision = 8, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "is_prime_early_access")
    var isPrimeEarlyAccess: Boolean = false
)