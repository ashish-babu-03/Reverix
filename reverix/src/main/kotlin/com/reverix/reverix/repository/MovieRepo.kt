package com.reverix.reverix.repository

import com.reverix.reverix.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Long> {
    fun findByTitleContainingIgnoreCase(title: String): List<Movie>
    fun findByGenreContainingIgnoreCase(genre: String): List<Movie>
    fun findByIsRentableTrue(): List<Movie>
}