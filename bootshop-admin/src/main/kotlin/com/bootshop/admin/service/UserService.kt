package com.bootshop.admin.service

import com.bootshop.admin.web.req.*
import com.bootshop.common.model.User

interface UserService {
    fun register(request: RegisterRequest): User
    fun confirmActiveAccount(key: String): Boolean
    fun removeNonActivatedUsers()
    fun resetPassword(rq: ResetPasswordRequest): Boolean
    fun requestForgotPassword(req:ForgotPassReq):User
    fun forgotPasswordFinish(req:KeyAndPassword):User
    fun remainTimeValidForgotPassword(email:String):Long
    fun changePassword(req:ChangePasswordReq):Boolean
}