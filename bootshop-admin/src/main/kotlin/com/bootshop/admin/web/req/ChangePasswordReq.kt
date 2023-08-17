package com.bootshop.admin.web.req

import jakarta.validation.constraints.NotBlank

data class ChangePasswordReq(@NotBlank val currentPassword:String, @NotBlank val newPassword:String)
