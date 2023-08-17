package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank

data class KeyAndPassword(
    @NotBlank val key:String,
    @NotBlank val password:String,
)
