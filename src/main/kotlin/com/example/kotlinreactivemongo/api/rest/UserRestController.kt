package com.example.kotlinreactivemongo.api.rest

import com.example.kotlinreactivemongo.config.security.errors.UserServiceException
import com.example.kotlinreactivemongo.config.security.model.User
import com.example.kotlinreactivemongo.config.security.service.UserReactiveCrudRepository
import org.bson.types.ObjectId
import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import javax.validation.Valid
import java.net.URI

import java.lang.String.format
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.RequestMethod.*

@RestController
@RequestMapping(path = arrayOf("/api/rest/user"), produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
class UserRestController(private val repo: UserReactiveCrudRepository) {

    /**
     * Query for all users.
     *
     *
     * This method is idempotent.
     *
     * @return HTTP 200 if users found or HTTP 204 otherwise.
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = arrayOf(GET))
    fun allUsers(): Mono<ResponseEntity<List<User>>> {

        return repo.findAll().collectList()
                .filter { users -> users.size > 0 }
                .map<ResponseEntity<List<User>>> { users -> ok<List<User>>(users) }
                .defaultIfEmpty(noContent().build<List<User>>())
    }

    /**
     * Create a new user.
     *
     * @param newUser
     * The user to create.
     *
     * @return HTTP 201, the header Location contains the URL of the created
     * user.
     */
    @RequestMapping(method = arrayOf(POST), consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    fun addUser(@RequestBody @Valid newUser: User): Mono<ResponseEntity<*>> {

        return Mono.justOrEmpty(newUser.id)
                .flatMap { id -> repo.existsById(id) }
                .defaultIfEmpty(java.lang.Boolean.FALSE)
                .flatMap { exists ->

                    if (exists) {
                        throw UserServiceException(HttpStatus.BAD_REQUEST,
                                "User already exists, to update an existing user use PUT instead.")
                    }

                    repo.save(newUser).map { saved -> created(URI.create(format("/users/%s", saved.id))).body(saved)}
                }
    }

    /**
     * Update an existing user.
     *
     *
     * This method is idempotent.
     *
     *
     *
     * @param id
     * The id of the user to update.
     * @param userToUpdate
     * The User object containing the updated version to be
     * persisted.
     *
     * @return HTTP 204 otherwise HTTP 400 if the user does not exist.
     */
    @RequestMapping(method = arrayOf(PUT), value = "/{id}", consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    fun updateUser(@PathVariable id: ObjectId,
                   @RequestBody @Valid userToUpdate: User): Mono<ResponseEntity<*>> {

        return repo.existsById(id).flatMap { exists ->

            if (!exists) {
                throw UserServiceException(HttpStatus.BAD_REQUEST,
                        "User does not exist, to create a new user use POST instead.")
            }

            repo.save<User>(userToUpdate).then(Mono.just(noContent().build<Any>()))
        }
    }

    /**
     * Delete a user.
     *
     *
     * This method is idempotent, if it's called multiples times with the same
     * id then the first call will delete the user and subsequent calls will
     * be silently ignored.
     *
     * @param id
     * The id of the user to delete.
     * @return HTTP 204
     */
    @RequestMapping(method = arrayOf(DELETE), value = "/{id}")
    fun deleteUser(@PathVariable id: ObjectId): Mono<ResponseEntity<*>> {

        val noContent = Mono.just<ResponseEntity<*>>(noContent().build<Any>())

        return repo.existsById(id)
                .filter { java.lang.Boolean.valueOf(it) } // Delete only if user exists
                .flatMap { exists -> repo.deleteById(id).then(noContent) }
                .switchIfEmpty(noContent)
    }

    @PostMapping("/create")
    internal fun create(@RequestBody userStream: Publisher<User>): Mono<Void> {
        return this.repo.saveAll(userStream).then()
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    internal fun list(): Flux<User> {
        return this.repo.findAll()
    }

    @GetMapping("/{id}")
    internal fun findById(@PathVariable id: ObjectId): Mono<User> {
        return this.repo.findById(id)
    }
}