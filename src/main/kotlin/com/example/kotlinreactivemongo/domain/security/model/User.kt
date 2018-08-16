package com.example.kotlinreactivemongo.domain.security.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import kotlin.collections.ArrayList


@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class User @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
constructor(
        @field:JsonSerialize(using = ToStringSerializer::class)
        //@JsonIgnore
        val id: ObjectId?,
        private val username: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        private val password: String,
        val roles: List<String> = ArrayList(),
        val enabled: Boolean,
//        @JsonIgnore
        var lastPasswordResetDate: Date
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

    //    @JsonIgnore
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