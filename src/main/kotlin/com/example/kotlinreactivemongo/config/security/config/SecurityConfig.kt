package com.example.kotlinreactivemongo.config.security.config

import com.example.kotlinreactivemongo.config.security.auth.UnauthorizedAuthenticationEntryPoint
import com.example.kotlinreactivemongo.config.security.encoder.CustomPasswordEncoder
import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationWebFilter
import com.example.kotlinreactivemongo.config.security.jwt.JwtTokenUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  authenticationWebFilter: JwtAuthenticationWebFilter,
                                  entryPoint: UnauthorizedAuthenticationEntryPoint): SecurityWebFilterChain {
        // We must override AuthenticationEntryPoint because if AuthenticationWebFilter didn't kicked in
        // (i.e. there are no required headers) then default behavior is to display HttpBasicAuth,
        // so we just return unauthorized to override it.
        // Filter tries to authenticate each request if it contains required headers.
        // Finally, we disable all default security.
        http
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange()
//                .pathMatchers("/**").permitAll()
                .pathMatchers("/resources/**",
                        "/webjars/**",
                        "/auth/**",
                        "/test/ws",
                        "/favicon.ico")
                .permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable()
        return http.build()
    }

    @Bean
    fun securityContextRepository(): WebSessionServerSecurityContextRepository {
        return WebSessionServerSecurityContextRepository()
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return CustomPasswordEncoder()
    }

    @Bean
    fun jwtTokenUtil(): JwtTokenUtil {
        return JwtTokenUtil("test", 100000L)
    }

}