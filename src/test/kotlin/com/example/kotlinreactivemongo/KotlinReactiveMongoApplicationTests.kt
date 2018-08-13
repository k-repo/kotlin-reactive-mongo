package fr.kza.backend.api.web

import com.example.kotlinreactivemongo.KotlinReactiveMongoApplication
import com.example.kotlinreactivemongo.api.rest.TestRestController
import com.example.kotlinreactivemongo.config.mongo.MongoConfiguration
import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationRequest
import com.example.kotlinreactivemongo.config.security.service.UserReactiveCrudRepository
import com.example.kotlinreactivemongo.config.web.WebConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest(TestRestController::class)
//@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = arrayOf(KotlinReactiveMongoApplication::class, MongoConfiguration::class))
class BackendApplicationTests {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private val webTestClient: WebTestClient? = null

    @Test
    fun fetchUsers() {
        val jwtAuthenticationRequest = JwtAuthenticationRequest("jdev", "jdev")
        val result1 = webTestClient!!
                .post().uri("/auth/token")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(jwtAuthenticationRequest), JwtAuthenticationRequest::class.java)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .returnResult()
                .toString()

        val ticket = result1.split("token".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()


        val token = ticket[2].split("\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]

        println("!!!!!!")
        println(token)
        println("!!!!!!")

        val result = webTestClient
                .get().uri("/test/user/jdev")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer $token")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("username").isEqualTo("jdev")
                .jsonPath("firstname").isEqualTo("Joe")
                .jsonPath("lastname").isEqualTo("Developer")
                .jsonPath("email").isEqualTo("dev@transempiric.com")
                .returnResult()
                .toString()

        println(result)

        logger.info(token)
    }

}
