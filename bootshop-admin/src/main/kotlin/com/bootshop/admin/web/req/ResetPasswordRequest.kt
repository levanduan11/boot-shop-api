package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @NotBlank val oldPass: String,
    @NotBlank val newPass: String,
    val username: String? = null,
)
