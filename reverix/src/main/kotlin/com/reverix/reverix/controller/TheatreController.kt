package com.reverix.reverix.controller

import com.reverix.reverix.entities.Theatre
import com.reverix.reverix.entities.VibeType
import com.reverix.reverix.service.TheatreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/theatres")
class TheatreController(
    private val theatreService: TheatreService
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Theatre>> =
        ResponseEntity.ok(theatreService.getAllTheatres())

    @GetMapping("/city/{city}")
    fun getByCity(@PathVariable city: String): ResponseEntity<List<Theatre>> =
        ResponseEntity.ok(theatreService.getTheatresByCity(city))

    @GetMapping("/city/{city}/vibe/{vibeType}")
    fun getByVibe(
        @PathVariable city: String,
        @PathVariable vibeType: VibeType
    ): ResponseEntity<List<Theatre>> =
        ResponseEntity.ok(theatreService.getTheatresByVibe(city, vibeType))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Theatre> =
        ResponseEntity.ok(theatreService.getTheatreById(id))

    @GetMapping("/recommend")
    fun recommend(
        @RequestParam city: String,
        @RequestParam groupType: String,
        @RequestParam(required = false, defaultValue = "") movieGenre: String
    ): ResponseEntity<List<Theatre>> =
        ResponseEntity.ok(
            theatreService.recommendTheatres(city, groupType, movieGenre)
        )
}