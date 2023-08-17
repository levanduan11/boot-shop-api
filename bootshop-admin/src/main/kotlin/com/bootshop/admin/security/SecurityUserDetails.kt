package com.bootshop.admin.security

import com.bootshop.common.model.Role
import com.bootshop.common.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class SecurityUserDetails(
    val user: User
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        user.roles
            .map(Role::name)
            .map(::SimpleGrantedAuthority)
            .toMutableList()

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.enabled

    fun getFullName(): String = "${user.firstName} ${user.lastName}"
}
