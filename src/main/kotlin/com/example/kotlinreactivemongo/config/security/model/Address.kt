package com.example.kotlinreactivemongo.config.security.model

import com.example.kotlinreactivemongo.config.security.errors.UserServiceException
import org.springframework.http.HttpStatus
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(Include.NON_NULL)
data class Address(val streetNumber: Int?, val streetName: String?, val city: String?, val zipcode: String?, val stateOrProvince: String?,
                   val country: String?) {



    class Builder(private val country: String?) {

        internal var streetNumber: Int? = null
        internal var streetName: String? = null
        internal var city: String? = null
        internal var zipcode: String? = null
        internal var stateOrProvince: String? = null

        init {
            if (country == null) {
                throw UserServiceException(HttpStatus.BAD_REQUEST, "Country can not be null.")
            }
        }

        fun withStreetNumber(streetNumber: Int?): Builder {
            this.streetNumber = streetNumber
            return this
        }

        fun withStreetName(streetName: String): Builder {
            this.streetName = streetName
            return this
        }

        fun withCity(city: String): Builder {
            this.city = city
            return this
        }

        fun withZipcode(zipcode: String): Builder {
            this.zipcode = zipcode
            return this
        }

        fun withStateOrProvince(stateOrProvince: String): Builder {
            this.stateOrProvince = stateOrProvince
            return this
        }

        fun build(): Address {
            return Address(streetNumber, streetName, city, zipcode, stateOrProvince, country)
        }
    }

    companion object {

        fun ofCountry(country: String): Builder {
            return Builder(country)
        }

        fun from(address: Address): Builder {
            val builder = Builder(address.country)
            builder.streetNumber = address.streetNumber
            builder.streetName = address.streetName
            builder.zipcode = address.zipcode
            builder.city = address.city
            builder.stateOrProvince = address.stateOrProvince
            return builder
        }
    }
}