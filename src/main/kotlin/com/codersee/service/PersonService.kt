package com.codersee.service

import com.codersee.model.Person
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

class PersonService {

    private val client = KMongo.createClient()
    private val database = client.getDatabase("person")
    private val personCollection = database.getCollection<Person>()

    fun create(person: Person): Id<Person>? {
        personCollection.insertOne(person)
        return person.id
    }

    fun findAll(): List<Person> =
        personCollection.find()
            .toList()

    fun findById(id: String): Person? {
        val bsonId: Id<Person> = ObjectId(id).toId()
        return personCollection
            .findOne(Person::id eq bsonId)
    }

    fun findByName(name: String): List<Person> {
        val nonCaseInsensitiveFilter = Person::name regex name
        val caseInsensitiveTypeSafeFilter = (Person::name).regex(name, "i")

        val nonTypeSafeFilter = "{name:{'\$regex' : '$name', '\$options' : 'i'}}"

        val withAndOperator = personCollection.find(
            and(Person::name regex name, Person::age gt 40)
        )

        val implicitAndOperator = personCollection.find(
            Person::name regex name, Person::age gt 40
        )

        val withOrOperator = personCollection.find(
            or(Person::name regex name, Person::age gt 40)
        )

        return personCollection.find(nonTypeSafeFilter)
            .toList()
    }

    fun updateById(id: String, request: Person): Boolean =
        findById(id)
            ?.let { person ->
                val updateResult = personCollection.replaceOne(person.copy(name = request.name, age = request.age))
                updateResult.modifiedCount == 1L
            } ?: false

    fun deleteById(id: String): Boolean {
        val deleteResult = personCollection.deleteOneById(ObjectId(id))
        return deleteResult.deletedCount == 1L
    }
}