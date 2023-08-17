package com.bootshop.admin

import com.bootshop.admin.config.MailConfig
import com.bootshop.admin.constants.AppConstants
import com.bootshop.admin.model.Mail
import com.bootshop.admin.repository.PersonRepository
import com.bootshop.admin.repository.RoleRepository
import com.bootshop.admin.repository.UserRepository
import com.bootshop.admin.service.MailService
import com.bootshop.common.model.Person
import com.bootshop.common.model.Role
import com.bootshop.common.model.User
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cglib.core.Local
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*


@SpringBootApplication
@EntityScan(value = ["com.bootshop.common.model"])
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(value = [MailConfig::class])
class BootshopAdminApplication(
	val roleRepository: RoleRepository,
	val userRepository: UserRepository,
	val messageSource: MessageSource,
	val mailService: MailService,
	val mailConfig: MailConfig
){

	@Bean
	fun runner():ApplicationRunner = ApplicationRunner {


	}
}

fun main(args: Array<String>) {
	runApplication<BootshopAdminApplication>(*args)
}
