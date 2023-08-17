package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank

data class RefreshTokenReq(@NotBlank val refreshToken:String,@NotBlank val username:String)
