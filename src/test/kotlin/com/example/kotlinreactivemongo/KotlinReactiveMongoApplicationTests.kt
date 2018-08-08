package com.example.kotlinreactivemongo

import junit.framework.Assert.assertEquals
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinReactiveMongoApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    @Before
    fun init() {
        userRepository.deleteAll()
    }

    @Test
    fun testUserRouter() {
        var user = User(ObjectId().toString(), "username1", "password", "username1@mail.com")

        // POST
        webTestClient.post().uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user), User::class.java)
                .exchange()
                .expectStatus().isOk
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$[0].username").isEqualTo("username1")
                .jsonPath("$[0].email").isEqualTo("username1@mail.com")

        StepVerifier.create(userRepository.findAll())
                .assertNext {
                    assertEquals("username1", it.username)
                    assertEquals("username1@mail.com", it.email)
                }
                .expectComplete()
                .verify()


        var id0 = userRepository.findAll().toStream().findFirst().get().id
        var user2 = User(id0, "SUPER_USER", "password", "username1@mail.com")


        // PUT
        webTestClient.put().uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user2), User::class.java)
                .exchange()
                .expectStatus().isOk
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$[0].username").isEqualTo("SUPER_USER")
                .jsonPath("$[0].email").isEqualTo("username1@mail.com")


        // GET ALL
        webTestClient.get().uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$[0].username").isEqualTo("SUPER_USER")
                .jsonPath("$[0].email").isEqualTo("username1@mail.com")

        var id = userRepository.findAll().toStream().findFirst().get().id

        // DLEETE
        webTestClient.delete().uri("/users/$id")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk

    }

}
