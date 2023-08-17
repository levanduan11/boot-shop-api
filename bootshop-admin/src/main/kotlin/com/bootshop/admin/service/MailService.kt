package com.bootshop.admin.service

import com.bootshop.admin.model.Mail

interface MailService {
    fun sendMail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean)
    fun senMail(mail: Mail, template: String, title: String)
    fun sendActivationMail(mail: Mail)
    fun sendForgotPasswordMail(mail: Mail)
}