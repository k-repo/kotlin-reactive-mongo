package com.example.kotlinreactivemongo.api.socket

import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationToken
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*


abstract class AbstractWebSocketHandler : WebSocketHandler {

    protected var authorizedRoles: ArrayList<String>? = ArrayList()

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.handshakeInfo.principal.filter({ this.isAuthorized(it) }).then(doHandle(session))
    }

    private fun isAuthorized(principal: Principal?): Boolean {
        var jwtAuthenticationToken: JwtAuthenticationToken
        if (principal != null && principal is JwtAuthenticationToken) {
            jwtAuthenticationToken = principal
        } else {
            throw AccessDeniedException("Invalid Token...")
        }

        if (jwtAuthenticationToken == null || !jwtAuthenticationToken.isAuthenticated) throw AccessDeniedException("Invalid Token...")

        val hasRoles = this.hasRoles(jwtAuthenticationToken.authorities)
        if (!hasRoles) throw AccessDeniedException("Not Authorized...")

        return true
    }

    private fun hasRoles(grantedAuthorities: Collection<GrantedAuthority>?): Boolean {
        if (this.authorizedRoles == null || this.authorizedRoles!!.isEmpty()) return true
        if (grantedAuthorities == null || grantedAuthorities.isEmpty()) return false

        for (role in authorizedRoles!!) {
            for (grantedAuthority in grantedAuthorities) {
                if (role.equals(grantedAuthority.authority, ignoreCase = true)) return true
            }
        }

        return false
    }

    internal abstract fun doHandle(session: WebSocketSession): Mono<Void>
}