package com.bootshop.common.model

import jakarta.persistence.*

@Entity
@Table(name = "persons")
data class Person(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,
    val name: String
) {
    constructor(name: String) : this(null, name)
}
