package com.reverix.reverix.repository

import com.reverix.reverix.entity.Theatre
import com.reverix.reverix.entity.VibeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TheatreRepository : JpaRepository<Theatre, Long> {
    fun findByCity(city: String): List<Theatre>
    fun findByCityAndVibeType(city: String, vibeType: VibeType): List<Theatre>
}