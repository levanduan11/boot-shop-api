package com.bootshop.admin.security

import com.bootshop.admin.repository.UserRepository
import com.bootshop.common.model.User
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class SecurityUserDetailService(val userRepository: UserRepository) : UserDetailsService {
    companion object {
        private val log = LoggerFactory.getLogger(SecurityUserDetailService::class.java)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        log.info("Authenticating {}", username)
        return userRepository.findByUsername(username)
            .map(::createSecurityUser)
            .orElseThrow { UsernameNotFoundException("Not found user with username: ${username}") }

    }

    fun createSecurityUser(user: User): UserDetails = SecurityUserDetails(user)

}