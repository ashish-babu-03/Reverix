package com.reverix.reverix.service

import com.reverix.reverix.config.JwtUtil
import com.reverix.reverix.dto.AuthResponse
import com.reverix.reverix.dto.LoginRequest
import com.reverix.reverix.dto.RegisterRequest
import com.reverix.reverix.entities.User
import com.reverix.reverix.repos.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already registered")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phone = request.phone
        )

        userRepository.save(user)

        val token = jwtUtil.generateToken(user.email, user.role.name)
        return AuthResponse(
            token = token,
            email = user.email,
            name = user.name,
            role = user.role.name
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("Invalid email or password")
        }

        val token = jwtUtil.generateToken(user.email, user.role.name)
        return AuthResponse(
            token = token,
            email = user.email,
            name = user.name,
            role = user.role.name
        )
    }
}