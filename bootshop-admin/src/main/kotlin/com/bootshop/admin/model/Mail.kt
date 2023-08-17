package com.bootshop.admin.model

data class Mail(
    var to:String,
    var subject:String="",
    var content:String="",
    var multipart:Boolean = false,
    var html:Boolean = true,
    var data:Map<String,Any> = mapOf()
)
