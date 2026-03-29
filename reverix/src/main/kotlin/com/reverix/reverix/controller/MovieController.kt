package com.reverix.reverix.controller

import com.reverix.reverix.entity.Movie
import com.reverix.reverix.service.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/movies")
class MovieController(
    private val movieService: MovieService
) {

    @GetMapping("/now-playing")
    fun getNowPlaying(): ResponseEntity<List<Movie>> {
        return ResponseEntity.ok(movieService.getNowPlaying())
    }

    @GetMapping("/popular")
    fun getPopular(): ResponseEntity<List<Movie>> {
        return ResponseEntity.ok(movieService.getPopular())
    }

    @GetMapping("/search")
    fun search(@RequestParam query: String): ResponseEntity<List<Movie>> {
        return ResponseEntity.ok(movieService.searchMovies(query))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Movie> {
        return ResponseEntity.ok(movieService.getMovieById(id))
    }

    @GetMapping("/rentable")
    fun getRentable(): ResponseEntity<List<Movie>> {
        return ResponseEntity.ok(movieService.getRentableMovies())
    }
}
