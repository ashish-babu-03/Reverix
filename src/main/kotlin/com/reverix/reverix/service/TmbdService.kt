package com.reverix.reverix.service

import com.reverix.reverix.dto.TmdbMovie
import com.reverix.reverix.dto.TmdbMovieResponse
import com.reverix.reverix.entity.Movie
import com.reverix.reverix.repository.MovieRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDate

@Service
class TmdbService(
    private val movieRepository: MovieRepository
) {

    @Value("\${tmdb.api.key}")
    private lateinit var apiKey: String

    @Value("\${tmdb.api.base-url}")
    private lateinit var baseUrl: String

    @Value("\${tmdb.image.base-url}")
    private lateinit var imageBaseUrl: String

    private val restTemplate = RestTemplate()

    // Genre map — TMDb genre IDs to names
    private val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation",
        35 to "Comedy", 80 to "Crime", 99 to "Documentary",
        18 to "Drama", 10751 to "Family", 14 to "Fantasy",
        36 to "History", 27 to "Horror", 10402 to "Music",
        9648 to "Mystery", 10749 to "Romance", 878 to "Science Fiction",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War",
        37 to "Western"
    )

    fun fetchNowPlayingMovies(): List<Movie> {
        val url = "$baseUrl/movie/now_playing?api_key=$apiKey&language=en-US&page=1"
        val response = restTemplate.getForObject<TmdbMovieResponse>(url)
        return response?.results?.map { tmdbMovie ->
            syncMovieToDB(tmdbMovie)
        } ?: emptyList()
    }

    fun fetchPopularMovies(): List<Movie> {
        val url = "$baseUrl/movie/popular?api_key=$apiKey&language=en-US&page=1"
        val response = restTemplate.getForObject<TmdbMovieResponse>(url)
        return response?.results?.map { tmdbMovie ->
            syncMovieToDB(tmdbMovie)
        } ?: emptyList()
    }

    fun searchMovies(query: String): List<Movie> {
        val url = "$baseUrl/search/movie?api_key=$apiKey&query=$query&language=en-US"
        val response = restTemplate.getForObject<TmdbMovieResponse>(url)
        return response?.results?.map { tmdbMovie ->
            syncMovieToDB(tmdbMovie)
        } ?: emptyList()
    }

    private fun syncMovieToDB(tmdbMovie: TmdbMovie): Movie {
        val existing = movieRepository.findAll()
            .find { it.tmdbId == tmdbMovie.id.toString() }

        if (existing != null) return existing

        val genres = tmdbMovie.genreIds
            .mapNotNull { genreMap[it] }
            .joinToString(", ")

        val movie = Movie(
            tmdbId = tmdbMovie.id.toString(),
            title = tmdbMovie.title,
            description = tmdbMovie.overview,
            posterUrl = tmdbMovie.posterPath?.let { "$imageBaseUrl$it" },
            rating = tmdbMovie.voteAverage,
            genre = genres,
            language = tmdbMovie.originalLanguage,
            releaseDate = parseDate(tmdbMovie.releaseDate),
            moodTags = generateMoodTags(genres)
        )

        return movieRepository.save(movie)
    }

    private fun parseDate(dateStr: String?): LocalDate? {
        return try {
            if (dateStr.isNullOrBlank()) null
            else LocalDate.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun generateMoodTags(genres: String): String {
        val tags = mutableListOf<String>()
        if (genres.contains("Action") || genres.contains("Adventure"))
            tags.add("energetic")
        if (genres.contains("Comedy"))
            tags.add("fun")
        if (genres.contains("Romance"))
            tags.add("romantic")
        if (genres.contains("Horror") || genres.contains("Thriller"))
            tags.add("thrilling")
        if (genres.contains("Drama"))
            tags.add("emotional")
        if (genres.contains("Family") || genres.contains("Animation"))
            tags.add("family-friendly")
        if (genres.contains("Science Fiction") || genres.contains("Fantasy"))
            tags.add("adventurous")
        return tags.joinToString(", ")
    }
}