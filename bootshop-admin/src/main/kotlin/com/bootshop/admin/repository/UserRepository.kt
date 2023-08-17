package com.bootshop.admin.repository

import com.bootshop.common.model.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.Optional

interface UserRepository : JpaRepository<User, Int> {

    @EntityGraph(attributePaths = ["roles"])
    fun findByUsername(username: String): Optional<User>

    @EntityGraph(attributePaths = ["roles"])
    fun findByEmail(email: String): Optional<User>

    fun findByActivationKey(key: String): Optional<User>

    @Query(
        "SELECT * FROM users u WHERE u.enabled = FALSE AND u.activation_key NOT NULL AND u.activation_date < ?1",
        nativeQuery = true
    )
    fun findAllByNonActivatedUser(time: LocalDateTime): List<User>

    fun findByResetKeyAndEnabledIsTrue(resetKey:String):Optional<User>
}