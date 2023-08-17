package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @NotBlank val username: String,
    @NotBlank val password: String,
    val rememberMe: Boolean = false,
)
