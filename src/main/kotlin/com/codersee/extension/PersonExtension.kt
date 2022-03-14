package com.codersee.extension

import com.codersee.model.Person
import com.codersee.model.PersonDto

fun Person.toDto(): PersonDto =
    PersonDto(
        id = this.id.toString(),
        name = this.name,
        age = this.age
    )

fun PersonDto.toPerson(): Person =
    Person(
        name = this.name,
        age = this.age
    )