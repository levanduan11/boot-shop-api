package com.bootshop.admin.config

import com.bootshop.admin.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            cors { }
            csrf { disable() }
            authorizeRequests {
                authorize(HttpMethod.POST, "/api/auth/authenticate/**", permitAll)
                authorize(HttpMethod.POST, "/api/auth/register/**", permitAll)
                authorize(HttpMethod.GET, "/api/auth/activate/**", permitAll)
                authorize(HttpMethod.POST, "/api/auth/reset/**", permitAll)
                authorize(HttpMethod.POST, "/api/auth/forgot-password-init/**", permitAll)
                authorize(HttpMethod.POST, "/api/auth/forgot-password-finish/**", permitAll)
                authorize(HttpMethod.POST, "/api/auth/refresh-token/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
//            exceptionHandling {
//                authenticationEntryPoint= AuthenticationEntryPoint{
//                    req,res,ex ->
//                    println(ex)
//                    println(ex.message)
//                }
//            }
        }
        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfig = CorsConfiguration().apply {
            allowCredentials = true
            allowedOrigins = listOf("http://localhost:4200", "http://localhost:3000")
            allowedHeaders = listOf(
                "Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
            )
            exposedHeaders = listOf(
                "Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        }
        val urlBasedConfig = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }
        return CorsFilter(urlBasedConfig)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}