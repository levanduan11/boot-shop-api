package com.bootshop.admin.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import java.util.HashMap
import java.util.HashSet
import java.util.Optional
import kotlin.reflect.cast

class SecurityUtil {
    companion object {
        fun currentUser(): Optional<User> =
            Optional.ofNullable(SecurityContextHolder.getContext())
                .map { it.authentication }
                .filter { it.isAuthenticated }
                .map {
                    println(it)
                    it.principal
                }
                .map(User::class::cast)
    }
}
