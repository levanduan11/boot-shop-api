package com.bootshop.admin.web.res

data class LoginResponse(
    var token:String,
    var refreshToken:String,
    var firstLogin:Boolean
)
