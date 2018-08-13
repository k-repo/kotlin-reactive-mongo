package com.example.kotlinreactivemongo.config.security.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority


@Document
data class Role(@Id private val id: String? = null) : GrantedAuthority {
    override fun getAuthority(): String? {
        return id
    }
}