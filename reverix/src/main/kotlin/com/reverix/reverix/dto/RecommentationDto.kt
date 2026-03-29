package com.reverix.reverix.dto

data class MoodRequest(
    val mood: String,
    val city: String,
    val groupType: String,
    val groupSize: Int = 1,
    val preferredZone: String = "MIDDLE"
)

data class RecommendationResponse(
    val recommendedMovies: List<MovieRecommendation>,
    val recommendedTheatres: List<TheatreRecommendation>,
    val aiSuggestion: String,
    val seatZone: String,
    val moodSummary: String
)

data class MovieRecommendation(
    val id: Long,
    val title: String,
    val genre: String?,
    val rating: Double?,
    val posterUrl: String?,
    val moodTags: String?,
    val reasonForRecommendation: String
)

data class TheatreRecommendation(
    val id: Long,
    val name: String,
    val location: String,
    val vibeType: String,
    val screenSize: String,
    val avgRating: Double,
    val reasonForRecommendation: String
)