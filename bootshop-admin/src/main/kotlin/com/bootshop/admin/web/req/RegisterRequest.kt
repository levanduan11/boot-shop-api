package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RegisterRequest(
    @NotBlank val firstName: String,
    @NotBlank val lastName: String,
    @NotBlank val username: String,
    @NotBlank val email: String,
    @NotNull val roles: Set<String>,
)
