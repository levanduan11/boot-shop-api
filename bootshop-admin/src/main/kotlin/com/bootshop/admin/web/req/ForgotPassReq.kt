package com.bootshop.admin.web.req

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ForgotPassReq( @NotBlank @Email val email:String)
