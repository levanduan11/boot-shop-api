package com.bootshop.admin.security.jwt

import com.bootshop.admin.repository.UserRepository
import com.bootshop.admin.security.SecurityUserDetails
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.management.relation.Role

@Component
class TokenProvider(val userRepository: UserRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(TokenProvider::class.java)
        private const val AUTHORITIES_KEY = "auth"
        private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        private val jwtParser: JwtParser = Jwts.parserBuilder().setSigningKey(key).build()
        private val tokenValidity: Instant = Instant.now().plus(1, ChronoUnit.MINUTES)
        private val refreshToken: Instant = Instant.now().plus(10, ChronoUnit.MINUTES)
        private val tokenValidityRememberMe: Instant = Instant.now().plus(7, ChronoUnit.DAYS)
    }


    fun createToken(authentication: Authentication, rememberMe: Boolean): String {
        val authorities = authentication.authorities.joinToString(",", transform = GrantedAuthority::getAuthority)
        val principal = authentication.principal as SecurityUserDetails
        val fullName = principal.getFullName()
        val validity = if (rememberMe) {
            Date.from(tokenValidityRememberMe)
        } else {
            Date.from(tokenValidity)
        }
        return Jwts
            .builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .claim("fullName", fullName)
            .signWith(key)
            .setExpiration(validity)
            .compact()
    }

    fun createRefreshToken(): String {
        val validity = Date.from(refreshToken)
        return Jwts
            .builder()
            .signWith(key)
            .setExpiration(validity)
            .compact()
    }


    fun getAuthentication(token: String): Authentication {
        val claims = jwtParser.parseClaimsJws(token).body
        val authorities = claims[AUTHORITIES_KEY].toString().split(",")
            .map(::SimpleGrantedAuthority)
        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            jwtParser.parseClaimsJws(authToken)
            return true
        } catch (ex: IllegalArgumentException) {
            log.trace("Invalid Jwt token trace.", ex)
        } catch (ex: UnsupportedJwtException) {
            log.trace("UnsupportedJwtException.", ex)
        } catch (ex: MalformedJwtException) {
            log.trace("MalformedJwtException.", ex)
        } catch (ex: SignatureException) {
            log.trace("MalformedJwtException.", ex)
        }

        return false
    }

    fun createAccessTokenFromUsername(username: String): String {
        val user = userRepository.findByUsername(username)
            .filter(com.bootshop.common.model.User::enabled)
            .orElseThrow()
        val auths = user?.roles?.joinToString(separator = ",", transform = com.bootshop.common.model.Role::name)
        return Jwts
            .builder()
            .setSubject(username)
            .claim(AUTHORITIES_KEY, auths)
            .claim("fullName", "${user.firstName} ${user.lastName}")
            .signWith(key)
            .setExpiration(Date.from(tokenValidity))
            .compact()
    }

}