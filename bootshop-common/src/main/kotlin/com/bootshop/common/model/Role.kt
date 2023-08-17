package com.bootshop.common.model

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    val name:String,
):AbstractAuditable<Int>()
