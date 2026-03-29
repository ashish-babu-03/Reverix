package com.reverix.reverix.controller

import com.reverix.reverix.dto.MoodRequest
import com.reverix.reverix.dto.RecommendationResponse
import com.reverix.reverix.service.RecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recommend")
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping
    fun getRecommendations(
        @RequestBody request: MoodRequest
    ): ResponseEntity<RecommendationResponse> {
        return ResponseEntity.ok(
            recommendationService.getRecommendations(request)
        )
    }
}