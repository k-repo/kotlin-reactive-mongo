package com.example.kotlinreactivemongo.config.security.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.GrantedAuthority
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.security.core.userdetails.UserDetails
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import kotlin.collections.ArrayList


@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class User @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
constructor(
        @field:JsonSerialize(using = ToStringSerializer::class)
        //@JsonIgnore
        var id: ObjectId?,
        private val username: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        private val password: String,
        var roles: List<String> = ArrayList(),
        val enabled: Boolean,
        //@JsonIgnore
        val lastPasswordResetDate: Date
) : UserDetails {

    override fun getUsername(): String {
        return username
    }

    //@JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    //@JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    //@JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun getPassword(): String {
        return password
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = ArrayList<GrantedAuthority>(this.roles.size)
        for (role in this.roles) {
            authorities.add(SimpleGrantedAuthority(role))
        }
        return authorities
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}