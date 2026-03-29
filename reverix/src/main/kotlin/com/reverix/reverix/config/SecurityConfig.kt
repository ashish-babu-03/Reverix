package com.reverix.reverix.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                // ✅ Static files — added
                it.requestMatchers(
                    "/",
                    "/index.html",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/*.ico",
                    "/*.png",
                    "/*.json"
                ).permitAll()

                // Your existing API routes — unchanged
                it.requestMatchers(
                    "/api/auth/**",
                    "/api/movies/**",
                    "/api/theatres/**",
                    "/api/recommend/**",
                    "/api/shows/**",
                    "/api/seats/**",
                ).permitAll()

                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration
    ): AuthenticationManager = config.authenticationManager
}