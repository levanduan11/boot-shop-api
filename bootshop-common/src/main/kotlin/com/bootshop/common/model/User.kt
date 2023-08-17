package com.bootshop.common.model

import jakarta.persistence.*
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonIgnore


@Entity
@Table(name = "users")
data class User(
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    @JsonIgnore
    var password: String = "",
    var email: String = "",
    var image: String = "",
    var phone: String = "",
    var enabled: Boolean = false,
    @JsonIgnore
    var activationKey: String? = null,
    @JsonIgnore
    var activationDate: LocalDateTime? = null,
    @JsonIgnore
    var resetKey: String? = null,
    @JsonIgnore
    var resetDate: LocalDateTime? = null,

    @JsonIgnore
    @ManyToMany(targetEntity = Role::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role> = emptySet(),
) : AbstractAuditable<Int>()

