package com.reverix.reverix.service

import com.reverix.reverix.entities.Movie
import com.reverix.reverix.repos.MovieRepository
import org.springframework.stereotype.Service

@Service
class MovieService(
    private val movieRepository: MovieRepository,
    private val tmdbService: TmdbService
) {

    fun getNowPlaying(): List<Movie> {
        return tmdbService.fetchNowPlayingMovies()
    }

    fun getPopular(): List<Movie> {
        return tmdbService.fetchPopularMovies()
    }

    fun searchMovies(query: String): List<Movie> {
        return tmdbService.searchMovies(query)
    }

    fun getMovieById(id: Long): Movie {
        return movieRepository.findById(id)
            .orElseThrow { RuntimeException("Movie not found") }
    }

    fun getRentableMovies(): List<Movie> {
        return movieRepository.findByIsRentableTrue()
    }
}