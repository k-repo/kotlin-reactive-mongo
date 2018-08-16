package com.example.kotlinreactivemongo.api.rest

import com.example.kotlinreactivemongo.config.security.errors.UserServiceException
import com.example.kotlinreactivemongo.domain.security.model.User
import com.example.kotlinreactivemongo.domain.security.repository.UserReactiveCrudRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.lang.String.format
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping(path = arrayOf("/api/rest/user"), produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
class UserRestController(private val repo: UserReactiveCrudRepository) {


    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = arrayOf(GET))
    fun allUsers(): Mono<ResponseEntity<List<User>>> {

        return repo.findAll().collectList()
                .filter { users -> users.size > 0 }
                .map<ResponseEntity<List<User>>> { users -> ok<List<User>>(users) }
                .defaultIfEmpty(noContent().build<List<User>>())
    }


    @RequestMapping(method = arrayOf(POST), consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    fun addUser(@RequestBody @Valid newUser: User): Mono<ResponseEntity<*>> {

        return Mono.justOrEmpty(newUser.id)
                .flatMap { id -> repo.existsById(id) }
                .defaultIfEmpty(false)
                .flatMap { exists ->

                    if (exists) {
                        throw UserServiceException(HttpStatus.BAD_REQUEST,
                                "User already exists, to update an existing user use PUT instead.")
                    }
                    repo.save(newUser)
                            .map { saved ->
                                created(URI.create(format("/users/%s", saved.id))).body(saved)
                            }
                }
    }


    @RequestMapping(method = arrayOf(PUT), consumes = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    fun updateUser(@RequestBody @Valid userToUpdate: User): Mono<ResponseEntity<*>> {

        return repo.existsById(userToUpdate.id!!).flatMap { exists ->

            if (!exists) {
                throw UserServiceException(HttpStatus.BAD_REQUEST,
                        "User does not exist, to create a new user use POST instead.")
            }

            repo.save(userToUpdate).then(Mono.just(noContent().build<Any>()))
        }
    }


    @RequestMapping(method = arrayOf(DELETE), value = "/{id}")
    fun deleteUser(@PathVariable id: ObjectId): Mono<ResponseEntity<*>> {

        val noContent = Mono.just<ResponseEntity<*>>(noContent().build<Any>())

        return repo.existsById(id)
                .filter { java.lang.Boolean.valueOf(it) } // Delete only if user exists
                .flatMap { exists -> repo.deleteById(id).then(noContent) }
                .switchIfEmpty(noContent)
    }

    @RequestMapping(method = arrayOf(GET), value = "/{id}")
    fun finUser(@PathVariable id: ObjectId): Mono<ResponseEntity<User>> {
        return Mono.justOrEmpty(id)
                .flatMap { id -> repo.findById(id) }
                .map<ResponseEntity<User>> { user -> ok<User>(user) }
                .defaultIfEmpty(noContent().build<User>())
    }

}