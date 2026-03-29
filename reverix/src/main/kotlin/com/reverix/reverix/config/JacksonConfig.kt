package com.reverix.reverix.config

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun hibernate6Module() = Hibernate6Module()
}