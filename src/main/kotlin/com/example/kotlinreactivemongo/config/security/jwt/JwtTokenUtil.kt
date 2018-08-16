package com.example.kotlinreactivemongo.config.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClock
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.Date
import kotlin.collections.HashMap
import kotlin.reflect.KFunction1

@Component
class JwtTokenUtil(@Value("\${jwt.secret}") private var secret: String,
                   @Value("\${jwt.expiration}") private var expiration: Long) : Serializable {

    val CLAIM_KEY_USERNAME = "sub"
    val CLAIM_KEY_AUDIENCE = "aud"
    val CLAIM_KEY_CREATED = "iat"

    val AUDIENCE_UNKNOWN = "unknown"
    val AUDIENCE_WEB = "web"
    val AUDIENCE_MOBILE = "mobile"
    val AUDIENCE_TABLET = "tablet"

    val clock = DefaultClock.INSTANCE

    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getIssuedAtDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getIssuedAt)
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }

    fun getAudienceFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getAudience)
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: KFunction1<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.call(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .body
    }

    private fun isTokenExpired(token: String): Boolean? {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(clock.now())
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created.before(lastPasswordReset)
    }

    private fun generateAudience(): String {
        return AUDIENCE_UNKNOWN
    }

    private fun ignoreTokenExpiration(token: String): Boolean {
        val audience = getAudienceFromToken(token)
        return AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return doGenerateToken(claims, userDetails.username, generateAudience())
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String, audience: String): String {
        val createdDate = clock.now()
        val expirationDate = calculateExpirationDate(createdDate)

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setAudience(audience)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date): Boolean? {
        val created = getIssuedAtDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && ((!isTokenExpired(token)!!) || ignoreTokenExpiration(token))
    }

    fun refreshToken(token: String): String {
        val createdDate = clock.now()
        val expirationDate = calculateExpirationDate(createdDate)

        val claims = getAllClaimsFromToken(token)
        claims.issuedAt = createdDate
        claims.expiration = expirationDate

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun validateToken(token: String): Boolean? {
        val username = getUsernameFromToken(token)
        val created = getIssuedAtDateFromToken(token)
        //final Date expiration = getExpirationDateFromToken(token);
        return username == username && (!isTokenExpired(token)!!)
    }

    private fun calculateExpirationDate(createdDate: Date): Date {
        return Date(createdDate.getTime() + expiration * 1000)
    }

}