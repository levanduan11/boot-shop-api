package com.bootshop.common.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "loginInfo")
data class LoginInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int?=null,
    var success:Boolean,
    var loginTime:LocalDateTime,
    var resetFirstTime:Boolean=false,
    @ManyToOne(targetEntity = User::class)
    @JoinColumn(name = "user_id")
    var user: User
)