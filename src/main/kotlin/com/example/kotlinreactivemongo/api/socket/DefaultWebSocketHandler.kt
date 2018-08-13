package com.example.kotlinreactivemongo.api.socket

import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

class DefaultWebSocketHandler : AbstractWebSocketHandler() {
    init {
        this.authorizedRoles?.addAll(Arrays.asList("ROLE_ADMIN"))
    }

    override fun doHandle(session: WebSocketSession): Mono<Void> {
        // Use retain() for Reactor Netty
        return session.send(session.receive()
                .doOnNext { it.retain() }
                .delayElements(Duration.ofSeconds(0)))
    }
}