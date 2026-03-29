package com.reverix.reverix.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = listOf("http://localhost:8080", "http://127.0.0.1:8080")
        config.allowedHeaders = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.exposedHeaders = listOf("Authorization")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}