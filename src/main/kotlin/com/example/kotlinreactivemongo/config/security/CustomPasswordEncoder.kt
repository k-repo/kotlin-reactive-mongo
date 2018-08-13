package com.example.kotlinreactivemongo.config.security

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom
import java.util.*

class CustomPasswordEncoder : PasswordEncoder {
    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        val decodedString = String(Base64.getDecoder().decode(rawPassword.toString()))
        return BCrypt.checkpw(decodedString, encodedPassword)
    }

    override fun encode(rawPassword: CharSequence?): String {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        val hashed : String = BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(14, random))
        return hashed
    }

}