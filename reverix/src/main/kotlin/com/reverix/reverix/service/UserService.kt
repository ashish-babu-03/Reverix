package com.reverix.reverix.service

import com.reverix.reverix.repos.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserIdByEmail(email: String): Long {
        return userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User not found") }
            .id
    }
}