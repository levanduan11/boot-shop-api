package com.bootshop.admin.service.impl

import com.bootshop.admin.config.MailConfig
import com.bootshop.admin.model.Mail
import com.bootshop.admin.repository.LoginInfoRepository
import com.bootshop.admin.repository.RoleRepository
import com.bootshop.admin.repository.UserRepository
import com.bootshop.admin.security.SecurityUserDetails
import com.bootshop.admin.security.SecurityUtil
import com.bootshop.admin.service.MailService
import com.bootshop.admin.service.UserService
import com.bootshop.admin.util.isAfterTime
import com.bootshop.admin.util.isBeforeTime
import com.bootshop.admin.web.req.*
import com.bootshop.common.exception.ConflictException
import com.bootshop.common.exception.DataNotFoundException
import com.bootshop.common.exception.ExpiredTokenException
import com.bootshop.common.exception.InvalidPasswordException
import com.bootshop.common.model.User
import com.bootshop.common.util.RandomUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailService: MailService,
    private val mailConfig: MailConfig,
    private val loginInfoRepository: LoginInfoRepository,
) : UserService {
    companion object {
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    override fun register(request: RegisterRequest): User {
        userRepository.findByEmail(request.email)
            .ifPresent {
                val removed = removeNotActivatedUser(it)
                if (!removed) throw ConflictException("Email already exists: ${request.email}")
            }
        userRepository.findByUsername(request.username)
            .ifPresent {
                val removed = removeNotActivatedUser(it)
                if (!removed) throw ConflictException("Username already exists: ${request.username}")
            }
        val pass = RandomUtils.randomToken(6)
        val token = UUID.randomUUID().toString()
        val user = User().apply {
            this.firstName = request.firstName
            this.lastName = request.lastName
            this.username = request.username
            this.password = passwordEncoder.encode(pass)
            this.email = request.email
            this.enabled = false
            this.activationKey = token
            this.activationDate = LocalDateTime.now()
            this.roles = request.roles.mapNotNull(roleRepository::findByName).toSet()
        }
        val savingUser = userRepository.save(user)
        // send mail
        val url = "${mailConfig.verifyHost}/auth/login?activationKey=$token"
        val mail = Mail(
            to = "lvduan1972@gmail.com", //user.email,
            data = mapOf(
                "username" to request.username,
                "password" to pass,
                "token" to token,
                "url" to url
            )
        )
        mailService.sendActivationMail(mail)
        return savingUser
    }

    override fun confirmActiveAccount(key: String): Boolean {
        userRepository.findByActivationKey(key)
            .map { user ->
                user.activationDate?.let { activationTime ->
                    val now = LocalDateTime.now()
                    val isInValidTime = isBeforeTime(activationTime.plusHours(1), now)
                    if (isInValidTime) {
                        userRepository.delete(user)
                        throw ExpiredTokenException("Token is expired")
                    }
                }
                user.activationKey = null
                user.activationDate = null
                user.enabled = true
                userRepository.save(user)
            }
            .orElseThrow { DataNotFoundException("Token Invalid!") }
        return true
    }

    /**
     * remove not activated user every day at 5:00 am
     */
    @Scheduled(cron = "0 0 5 * * *")
    override fun removeNonActivatedUsers() {
        userRepository.findAllByNonActivatedUser(LocalDateTime.now().minusHours(1))
            .forEach {
                log.info("Deleting non activated user: {}", it.username)
                userRepository.delete(it)
            }
    }

    private fun removeNotActivatedUser(user: User): Boolean {
        val now = LocalDateTime.now()
        val activationDate = user.activationDate!!
        if (user.enabled || isAfterTime(activationDate.plusHours(1), now)) return false
        userRepository.delete(user)
        userRepository.flush()
        return true
    }


    override fun resetPassword(rq: ResetPasswordRequest): Boolean {
        Optional
            .ofNullable(rq.username)
            .or { SecurityUtil.currentUser().map(org.springframework.security.core.userdetails.User::getUsername) }
            .flatMap(userRepository::findByUsername)
            .map { user ->
                if (rq.username != null) {
                    loginInfoRepository.findByUserIdAndResetFirstTimeIsTrue(user.id!!)
                        .ifPresent {
                            it.resetFirstTime = false
                            loginInfoRepository.save(it)
                        }
                }
                val passHash = user.password
                if (!passwordEncoder.matches(rq.oldPass, passHash)) {
                    throw InvalidPasswordException("password invalid!")
                }
                val newHashPass = passwordEncoder.encode(rq.newPass)
                user.password = newHashPass
                user
            }
            .map(userRepository::save)
            .orElseThrow { UsernameNotFoundException("username invalid") }
        return true
    }

    override fun requestForgotPassword(req: ForgotPassReq): User {
        return userRepository.findByEmail(req.email)
            .filter(User::enabled)
            .map { u ->
                u.resetDate = LocalDateTime.now()
                u.resetKey = UUID.randomUUID().toString()
                u
            }
            .map(userRepository::save)
            .orElseThrow { DataNotFoundException("Not found user with Email: ${req.email}") }
    }

    override fun forgotPasswordFinish(req: KeyAndPassword): User {
        return userRepository.findByResetKeyAndEnabledIsTrue(req.key)
            .map {
                if (isBeforeTime(it.resetDate!!, LocalDateTime.now().minusHours(1))) {
                    throw ExpiredTokenException("Token is expired")
                }
                it.resetDate = null
                it.resetKey = null
                it.password = passwordEncoder.encode(req.password)
                it
            }
            .map(userRepository::save)
            .orElseThrow { DataNotFoundException("Not found user") }
    }

    override fun remainTimeValidForgotPassword(email: String): Long {
        return userRepository.findByEmail(email)
            .filter(User::enabled)
            .map { user ->
                user.resetDate?.let { resetDate ->
                    val now = LocalDateTime.now()
                    val validThresold = resetDate.plusHours(1)
                    val diff = Duration.between(now, validThresold).toMinutes()
                    return@map if (diff > 0) diff else throw ExpiredTokenException("Token is expired")
                }
                throw RuntimeException("reset date must not be null")
            }
            .orElseThrow { DataNotFoundException("Not found user with email $email") }
    }

    override fun changePassword(req: ChangePasswordReq): Boolean {
        SecurityUtil.currentUser()
            .map(org.springframework.security.core.userdetails.User::getUsername)
            .flatMap(userRepository::findByUsername)
            .ifPresent {
                if (!passwordEncoder.matches(req.currentPassword,it.password)){
                    throw InvalidPasswordException("Password not match!")
                }
                it.password=passwordEncoder.encode(req.newPassword)
                userRepository.save(it)
            }
        return true
    }
}