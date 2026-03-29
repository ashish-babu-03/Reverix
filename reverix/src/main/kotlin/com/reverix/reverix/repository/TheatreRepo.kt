package com.reverix.reverix.repos

import com.reverix.reverix.entities.Theatre
import com.reverix.reverix.entities.VibeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TheatreRepository : JpaRepository<Theatre, Long> {
    fun findByCity(city: String): List<Theatre>
    fun findByCityAndVibeType(city: String, vibeType: VibeType): List<Theatre>
}