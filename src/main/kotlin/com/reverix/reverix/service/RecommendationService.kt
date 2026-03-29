package com.reverix.reverix.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.reverix.reverix.dto.*
import com.reverix.reverix.entity.Movie
import com.reverix.reverix.entity.SeatZone
import com.reverix.reverix.entity.Theatre
import com.reverix.reverix.repository.MovieRepository
import com.reverix.reverix.repository.TheatreRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RecommendationService(
    private val movieRepository: MovieRepository,
    private val theatreRepository: TheatreRepository,
    private val objectMapper: ObjectMapper
) {
    @Value("\${openrouter.api.key}")
    private lateinit var openRouterApiKey: String

    @Value("\${openrouter.api.url}")
    private lateinit var openRouterApiUrl: String

    @Value("\${openrouter.model}")                        // ✅ Add this
    private lateinit var openRouterModel: String          // ✅ Add this

    private val restTemplate = RestTemplate()

    fun getRecommendations(request: MoodRequest): RecommendationResponse {
        val allMovies = movieRepository.findAll().take(20)
        val allTheatres = theatreRepository.findByCity(request.city)

        val aiAnalysis = callAiForRecommendation(
            request, allMovies, allTheatres
        )

        val recommendedMovies = filterMoviesByMood(
            allMovies, request.mood, aiAnalysis.suggestedGenres
        )

        val recommendedTheatres = filterTheatresByGroup(
            allTheatres, request.groupType
        )

        val seatZone = determineSeatZone(
            request.groupType, request.groupSize, request.preferredZone
        )

        return RecommendationResponse(
            recommendedMovies = recommendedMovies.take(5).map { movie ->
                MovieRecommendation(
                    id = movie.id,
                    title = movie.title,
                    genre = movie.genre,
                    rating = movie.rating,
                    posterUrl = movie.posterUrl,
                    moodTags = movie.moodTags,
                    reasonForRecommendation = aiAnalysis.movieReason
                )
            },
            recommendedTheatres = recommendedTheatres.take(3).map { theatre ->
                TheatreRecommendation(
                    id = theatre.id,
                    name = theatre.name,
                    location = theatre.location,
                    vibeType = theatre.vibeType.name,
                    screenSize = theatre.screenSize.name,
                    avgRating = theatre.avgRating,
                    reasonForRecommendation = aiAnalysis.theatreReason
                )
            },
            aiSuggestion = aiAnalysis.fullSuggestion,
            seatZone = seatZone.name,
            moodSummary = aiAnalysis.moodSummary
        )
    }

    private fun callAiForRecommendation(
        request: MoodRequest,
        movies: List<Movie>,
        theatres: List<Theatre>
    ): AiAnalysis {
        val movieTitles = movies.joinToString(", ") {
            "${it.title}(${it.genre})"
        }
        val theatreNames = theatres.joinToString(", ") {
            "${it.name}(${it.vibeType})"
        }

        val prompt = """
            You are a movie recommendation AI for Reverix, a smart movie booking app.
            
            User mood: "${request.mood}"
            Group type: ${request.groupType}
            Group size: ${request.groupSize}
            City: ${request.city}
            
            Available movies: $movieTitles
            Available theatres: $theatreNames
            
            Based on the user's mood and group type, respond ONLY with this exact JSON:
            {
                "suggestedGenres": ["genre1", "genre2"],
                "moodSummary": "one line summary of user mood",
                "movieReason": "why these movies match the mood",
                "theatreReason": "why these theatres suit the group",
                "fullSuggestion": "friendly 2-3 sentence recommendation message"
            }
        """.trimIndent()

        return try {
            val requestBody = mapOf(
                "model" to openRouterModel,
                "messages" to listOf(
                    mapOf(
                        "role" to "user",
                        "content" to prompt
                    )
                )
            )

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers.setBearerAuth(openRouterApiKey)
            headers["HTTP-Referer"] = "http://localhost:8080"
            headers["X-Title"] = "Reverix"

            val entity = HttpEntity(requestBody, headers)

            val response = restTemplate.postForObject(
                openRouterApiUrl, entity, Map::class.java
            )

            val text = extractTextFromOpenRouterResponse(response)
            parseAiAnalysis(text)
        } catch (e: Exception) {
            println("OpenRouter AI error: ${e.message}")
            AiAnalysis(
                suggestedGenres = listOf("Drama", "Action"),
                moodSummary = "Looking for a great movie experience",
                movieReason = "Top rated movies matching your taste",
                theatreReason = "Best theatres for your group",
                fullSuggestion = "Here are our top picks for you tonight!"
            )
        }
    }

    private fun extractTextFromOpenRouterResponse(response: Map<*, *>?): String {
        return try {
            val choices = response?.get("choices") as? List<*>
            val first = choices?.firstOrNull() as? Map<*, *>
            val message = first?.get("message") as? Map<*, *>
            message?.get("content") as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseAiAnalysis(text: String): AiAnalysis {
        return try {
            val jsonStart = text.indexOf("{")
            val jsonEnd = text.lastIndexOf("}") + 1
            if (jsonStart == -1 || jsonEnd == 0) throw Exception("No JSON found")
            val json = text.substring(jsonStart, jsonEnd)
            objectMapper.readValue(json, AiAnalysis::class.java)
        } catch (e: Exception) {
            AiAnalysis(
                suggestedGenres = listOf("Action", "Comedy"),
                moodSummary = "Ready for a great movie",
                movieReason = "Great movies for your mood",
                theatreReason = "Perfect theatres for your group",
                fullSuggestion = "Enjoy your movie experience with Reverix!"
            )
        }
    }

    private fun filterMoviesByMood(
        movies: List<Movie>,
        moodPrompt: String,
        suggestedGenres: List<String>
    ): List<Movie> {
        val moodLower = moodPrompt.lowercase()
        return movies.filter { movie ->
            val genre = movie.genre?.lowercase() ?: ""
            val tags = movie.moodTags?.lowercase() ?: ""
            suggestedGenres.any { genre.contains(it.lowercase()) } ||
                    when {
                        moodLower.contains("happy") || moodLower.contains("fun") ->
                            tags.contains("fun") || genre.contains("comedy")
                        moodLower.contains("romantic") || moodLower.contains("love") ->
                            tags.contains("romantic") || genre.contains("romance")
                        moodLower.contains("excited") || moodLower.contains("thrill") ->
                            tags.contains("thrilling") || genre.contains("action")
                        moodLower.contains("sad") || moodLower.contains("emotional") ->
                            tags.contains("emotional") || genre.contains("drama")
                        moodLower.contains("adventur") ->
                            tags.contains("adventurous") || genre.contains("adventure")
                        else -> true
                    }
        }.sortedByDescending { it.rating }
    }

    private fun filterTheatresByGroup(
        theatres: List<Theatre>,
        groupType: String
    ): List<Theatre> {
        val preferredVibe = when (groupType.lowercase()) {
            "friends" -> "CELEBRATION"
            "family" -> "FAMILY"
            "couple", "partner" -> "DATE_NIGHT"
            "solo" -> "SILENT"
            else -> "SILENT"
        }
        val matched = theatres.filter {
            it.vibeType.name == preferredVibe
        }
        return if (matched.isEmpty()) {
            theatres.sortedByDescending { it.avgRating }
        } else {
            matched.sortedByDescending { it.avgRating }
        }
    }

    private fun determineSeatZone(
        groupType: String,
        groupSize: Int,
        preferredZone: String
    ): SeatZone {
        if (preferredZone != "MIDDLE") {
            return SeatZone.valueOf(preferredZone)
        }
        return when {
            groupType.lowercase() == "friends" && groupSize >= 4 -> SeatZone.MIDDLE
            groupType.lowercase() == "family" -> SeatZone.BACK
            groupType.lowercase() in listOf("couple", "partner") -> SeatZone.MIDDLE
            groupType.lowercase() == "solo" -> SeatZone.MIDDLE
            else -> SeatZone.MIDDLE
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class AiAnalysis(
    val suggestedGenres: List<String> = emptyList(),
    val moodSummary: String = "",
    val movieReason: String = "",
    val theatreReason: String = "",
    val fullSuggestion: String = ""
)