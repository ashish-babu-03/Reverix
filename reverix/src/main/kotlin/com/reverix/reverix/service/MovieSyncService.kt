package com.reverix.reverix.service

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(2)
class MovieSyncService(
    private val tmdbService: TmdbService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println("🎬 Syncing now-playing movies from TMDb...")
        try {
            val movies = tmdbService.fetchNowPlayingMovies()
            println("TMDb sync complete — ${movies.size} movies in DB.")
        } catch (e: Exception) {
            println("TMDb sync failed: ${e.message}")
        }
    }
}