package com.bootshop.admin.controller

import com.bootshop.admin.repository.PersonRepository
import com.bootshop.common.model.Person
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/persons")
//@CrossOrigin(value = ["http://localhost:4200","http://localhost:3000"])
class PersonController(val personRepository: PersonRepository){

    @GetMapping
    fun findAll():List<Person> = personRepository.findAll()
    @GetMapping("/{id}")
    fun findById(@PathVariable id:Int)=personRepository.findById(id).get()
}