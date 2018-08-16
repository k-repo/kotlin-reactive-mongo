package com.example.kotlinreactivemongo.config.security.jwt

import com.example.kotlinreactivemongo.config.security.auth.UnauthorizedAuthenticationEntryPoint
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationWebFilter(authenticationManager: ReactiveAuthenticationManager,
                                 converter: JwtAuthenticationConverter,
                                 entryPoint: UnauthorizedAuthenticationEntryPoint) : AuthenticationWebFilter(authenticationManager) {

    init {

        Assert.notNull(authenticationManager, "authenticationManager cannot be null")
        Assert.notNull(converter, "converter cannot be null")
        Assert.notNull(entryPoint, "entryPoint cannot be null")

        this.setAuthenticationConverter(converter)
        this.setAuthenticationFailureHandler(ServerAuthenticationEntryPointFailureHandler(entryPoint))
        this.setRequiresAuthenticationMatcher(JWTHeadersExchangeMatcher())
    }

    private class JWTHeadersExchangeMatcher : ServerWebExchangeMatcher {
        override fun matches(exchange: ServerWebExchange): Mono<ServerWebExchangeMatcher.MatchResult> {
            val request = Mono.just(exchange).map(ServerWebExchange::getRequest)

            /* Check for header "authorization" or parameter "token" */
            return request.map(ServerHttpRequest::getHeaders)
                    .filter { h -> h.containsKey("authorization") }
                    .flatMap { `$` -> ServerWebExchangeMatcher.MatchResult.match() }
                    .switchIfEmpty(request.map(ServerHttpRequest::getQueryParams)
                            .filter { h -> h.containsKey("token") }
                            .flatMap { `$` -> ServerWebExchangeMatcher.MatchResult.match() }
                            .switchIfEmpty(ServerWebExchangeMatcher.MatchResult.notMatch())
                    )
        }
    }
}