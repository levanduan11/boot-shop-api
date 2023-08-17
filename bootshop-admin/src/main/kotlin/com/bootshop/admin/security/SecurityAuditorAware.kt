package com.bootshop.admin.security

import com.bootshop.admin.constants.AppConstants
import com.bootshop.admin.repository.UserRepository
import org.springframework.data.domain.AuditorAware
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class SecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map { it.authentication }
            .filter { it.isAuthenticated }
            .map {
                if (it is UsernamePasswordAuthenticationToken) {
                    val principal = it.principal as org.springframework.security.core.userdetails.User
                    return@map principal.username
                }
                if (it is AnonymousAuthenticationToken){
                    return@map AppConstants.USER_REGISTER
                }
                it?.principal.toString()
            }
            .or { Optional.of(AppConstants.SYSTEM_USER) }
    }
}