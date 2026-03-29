package com.reverix.reverix.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbMovieResponse(
    val results: List<TmdbMovie> = emptyList(),
    @JsonProperty("total_pages")
    val totalPages: Int = 0,
    @JsonProperty("total_results")
    val totalResults: Int = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbMovie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    @JsonProperty("poster_path")
    val posterPath: String? = null,
    @JsonProperty("release_date")
    val releaseDate: String? = null,
    @JsonProperty("vote_average")
    val voteAverage: Double = 0.0,
    @JsonProperty("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language")
    val originalLanguage: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbGenreResponse(
    val genres: List<TmdbGenre> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbGenre(
    val id: Int = 0,
    val name: String = ""
)