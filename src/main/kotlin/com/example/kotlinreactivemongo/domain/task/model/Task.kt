package com.example.kotlinreactivemongo.domain.task.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Task(@Id
                @field:JsonSerialize(using = ToStringSerializer::class)
                val id: ObjectId = ObjectId(),
                val name: String,
                val dateBegin: Date,
                val dateEnd: Date)