package com.reverix.reverix.controller

import com.reverix.reverix.repos.ShowRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/shows")
class ShowController(
    private val showRepository: ShowRepository
) {

    @GetMapping
    fun getShowsByMovie(@RequestParam movieId: Long) =
        showRepository.findByMovieIdAndShowTimeAfter(movieId, LocalDateTime.now())
}