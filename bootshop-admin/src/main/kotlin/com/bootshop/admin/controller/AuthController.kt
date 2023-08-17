package com.bootshop.admin.controller

import com.bootshop.admin.config.MailConfig
import com.bootshop.admin.model.Mail
import com.bootshop.admin.repository.LoginInfoRepository
import com.bootshop.admin.repository.UserRepository
import com.bootshop.admin.security.jwt.JwtAuthenticationFilter
import com.bootshop.admin.security.jwt.TokenProvider
import com.bootshop.admin.service.MailService
import com.bootshop.admin.service.UserService
import com.bootshop.admin.web.req.*
import com.bootshop.admin.web.res.ApiResponse
import com.bootshop.admin.web.res.LoginResponse
import com.bootshop.common.model.LoginInfo
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = ["http://localhost:3000"])
class AuthController(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenProvider: TokenProvider,
    private val loginInfoRepository: LoginInfoRepository,
    private val mailService: MailService,
    private val mailConfig: MailConfig
) {
    companion object {
        private val log = LoggerFactory.getLogger(AuthController::class.java)
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): String {
        val u = userService.register(registerRequest)
        return "registered success with user id ${u.id}"
    }

    @PostMapping("/authenticate")
    fun authorize(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val credentials = UsernamePasswordAuthenticationToken(
            loginRequest.username,
            loginRequest.password
        )
        val authentication: Authentication
        val user = userRepository.findByUsername(loginRequest.username)
        var loginSuccess = false
        var count = 0L
        return try {
            authentication = authenticationManagerBuilder.`object`.authenticate(credentials)
            SecurityContextHolder.getContext().authentication = authentication
            loginSuccess = true
            val token = tokenProvider.createToken(authentication, loginRequest.rememberMe)
            val refreshToken=tokenProvider.createRefreshToken()
            val headers = HttpHeaders()
            headers[JwtAuthenticationFilter.AUTHORIZATION_HEADER] = "Bearer $token"
            val id = user.get().id!!
            val countResetFirstTime = loginInfoRepository.countResetFirstTime(id)
            val countLoginOK = loginInfoRepository.countLoginSuccessByUserId(id)
            count = countLoginOK
            val loginResponse = LoginResponse(
                token = token,
                refreshToken=refreshToken,
                firstLogin = (countLoginOK == 0L || countResetFirstTime > 0)
            )
            ResponseEntity(loginResponse, headers, HttpStatus.OK)
        } finally {

            user.ifPresent {
                val loginInfo = LoginInfo(
                    success = loginSuccess,
                    loginTime = LocalDateTime.now(),
                    user = it,
                    resetFirstTime = (count == 0L && loginSuccess)
                )
                loginInfoRepository.save(loginInfo)
                loginInfoRepository.flush()
            }
        }
    }

    @GetMapping("/activate")
    fun confirmActivation(@RequestParam("token") token: String): Boolean {
        return userService.confirmActiveAccount(key = token)
    }

    @PostMapping("/reset")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<ApiResponse> {
        userService.resetPassword(request)
        return ResponseEntity.ok(
            ApiResponse(status = true)
        )
    }

    @PostMapping("/forgot-password-init")
    fun initForgotPassword(@Valid @RequestBody req: ForgotPassReq): ResponseEntity<ApiResponse> {
        val user = userService.requestForgotPassword(req)
        val url = "${mailConfig.verifyHost}/auth/forgot-finish?resetKey=${user.resetKey}"
        val mail = Mail(
            to = "lvduan1972@gmail.com",
            data = mapOf("url" to url)
        )
        mailService.sendForgotPasswordMail(mail)
        return ResponseEntity
            .ok(ApiResponse(status = true, data = user))
    }

    @PostMapping("/forgot-password-finish")
    fun forgotPasswordFinish(@Valid @RequestBody req: KeyAndPassword): ResponseEntity<ApiResponse> {
        val user = userService.forgotPasswordFinish(req)
        return ResponseEntity
            .ok(ApiResponse(status = true, data = user))
    }

    @GetMapping("/forgot-password-remain-time")
    fun remainTimeValidForgotPassword(email: String): ResponseEntity<ApiResponse> {
        val res = userService.remainTimeValidForgotPassword(email)
        return ResponseEntity.ok(ApiResponse(status = true, data = res))
    }

    @PostMapping("/change-password")
    fun changePassword(@Valid @RequestBody req: ChangePasswordReq): ResponseEntity<ApiResponse> {
        userService.changePassword(req)
        return ResponseEntity.ok(ApiResponse(status = true))
    }

    @PostMapping("/refresh-token")
    fun getToken(@Valid @RequestBody req:RefreshTokenReq):ResponseEntity<ApiResponse>{
        val (token,username)=req
        val validToken=tokenProvider.validateToken(token)
        return if (validToken){
            val accessToken=tokenProvider.createAccessTokenFromUsername(username)
            val refreshToken=tokenProvider.createRefreshToken()
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = mapOf(
                        "token" to accessToken,
                        "refreshToken" to refreshToken
                    )
                ))
        }else{
            ResponseEntity.badRequest().body(ApiResponse(status = false))
        }
    }
}