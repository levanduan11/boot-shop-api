package com.bootshop.admin.security.jwt


import com.bootshop.admin.web.res.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {

    companion object {
        const val AUTHORIZATION_HEADER: String = "Authorization"
        private val log: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = resolveToken(request)
        jwt?.let {
            try {
                if (it.isNotBlank() && tokenProvider.validateToken(it)) {
                    val authentication = tokenProvider.getAuthentication(it)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (ex: ExpiredJwtException) {
                log.warn("ExpiredJwtException {}", ex.message)
                val res = ApiResponse(
                    message = "Token is Expired",
                    status = false,
                    httpStatus = HttpStatus.FORBIDDEN.value(),
                    errors = mapOf("token_expired" to true)
                )
                response.status=403
                response.setTrailerFields { mapOf("error" to res.toString()) }
                ObjectMapper().writeValue(response.outputStream, res)
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        request.getHeader(AUTHORIZATION_HEADER)?.let {
            if (it.isNotBlank() && it.startsWith("Bearer ")) {
                return it.substring(7)
            }
        }
        return null
    }
}