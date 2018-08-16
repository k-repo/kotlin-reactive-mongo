package com.example.kotlinreactivemongo.config.socket

import com.example.kotlinreactivemongo.api.socket.DefaultWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import java.util.*

@Configuration
class WebSocketRouter {
    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = HashMap<String, WebSocketHandler>()
        map["/api/ws/echotest"] = DefaultWebSocketHandler()

        val mapping = SimpleUrlHandlerMapping()
        mapping.initApplicationContext()
        mapping.order = 10
        mapping.urlMap = map
        return mapping
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter(webSocketService())
    }

    /*
       For Tomcat
        @Bean
        public WebSocketService webSocketService() {
            TomcatRequestUpgradeStrategy strategy = new TomcatRequestUpgradeStrategy();
            strategy.setMaxSessionIdleTimeout(0L);
            return new HandshakeWebSocketService(strategy);
        }
	*/

    @Bean
    fun webSocketService(): WebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }
}