package com.bootshop.admin.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.mail")
data class MailConfig(
    val username:String?,
    val verifyHost:String?
)
