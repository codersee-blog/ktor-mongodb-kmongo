package com.codersee.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class Person(
    @BsonId
    val id: Id<Person>? = null,
    val name: String,
    val age: Int
)
