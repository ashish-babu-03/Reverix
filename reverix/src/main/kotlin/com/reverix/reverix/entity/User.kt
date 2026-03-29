package com.reverix.reverix.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var name: String = "",

    @Column(nullable = false, unique = true, length = 150)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(length = 15)
    var phone: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class Role {
    USER, PRIME_USER, ADMIN
}