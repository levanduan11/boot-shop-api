package com.bootshop.admin.service.impl

import com.bootshop.admin.config.MailConfig
import com.bootshop.admin.model.Mail
import com.bootshop.admin.service.MailService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import java.util.Locale

@Service
class MailServiceImpl(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val messageSource: MessageSource,
    private val mailConfig: MailConfig
) : MailService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(MailServiceImpl::class.java)
    }

    @Async
    override fun sendMail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean) {
        if (mailConfig.username==null)return
        val mimeMessage = mailSender.createMimeMessage()
        val message = MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name())
        message.setTo(to)
        message.setFrom(mailConfig.username)
        message.setSubject(subject)
        message.setText(content, isHtml)
        mailSender.send(mimeMessage)

    }

    @Async
    override fun senMail(mail: Mail, template: String, title: String) {
        val locale = Locale.getDefault()
        val context = Context().apply {
            this.setVariables(mail.data)
            this.locale = locale
        }
        val content = templateEngine.process(template, context)
        val subject = messageSource.getMessage(title, null, locale)
        sendMail(mail.to, subject, content, isMultipart = false, isHtml = true)
    }

    @Async
    override fun sendActivationMail(mail: Mail) {
        log.info("send activation mail to {}", mail.to)
        senMail(mail, "mail/activation", "mail.activation.title")
    }

    @Async
    override fun sendForgotPasswordMail(mail: Mail) {
        log.info("sen forgot password mail to {}",mail.to)
        senMail(mail,"mail/forgot-password","mail.forgot-password.title")
    }
}