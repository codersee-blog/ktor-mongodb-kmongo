package com.codersee.plugins

import com.codersee.extension.toDto
import com.codersee.extension.toPerson
import com.codersee.model.ErrorResponse
import com.codersee.model.Person
import com.codersee.model.PersonDto
import com.codersee.service.PersonService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    val service = PersonService()

    routing {
        route("/person") {
            post {
                val request = call.receive<PersonDto>()
                val person = request.toPerson()

                service.create(person)
                    ?.let { userId ->
                        call.response.headers.append("My-User-Id-Header", userId.toString())
                        call.respond(HttpStatusCode.Created)
                    } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
            }

            get {
                val peopleList =
                    service.findAll()
                        .map(Person::toDto)

                call.respond(peopleList)
            }

            get("/{id}") {
                val id = call.parameters["id"].toString()

                service.findById(id)
                    ?.let { foundPerson -> call.respond(foundPerson.toDto()) }
                    ?: call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
            }

            put("/{id}") {
                val id = call.parameters["id"].toString()
                val personRequest = call.receive<PersonDto>()
                val person = personRequest.toPerson()

                val updatedSuccessfully = service.updateById(id, person)

                if (updatedSuccessfully) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"].toString()

                val deletedSuccessfully = service.deleteById(id)

                if (deletedSuccessfully) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
                }
            }
        }
    }
}
