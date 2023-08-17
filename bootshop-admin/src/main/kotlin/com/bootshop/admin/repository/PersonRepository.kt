package com.bootshop.admin.repository

import com.bootshop.common.model.Person
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person,Int>