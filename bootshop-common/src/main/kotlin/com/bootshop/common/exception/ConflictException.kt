package com.bootshop.common.exception

class ConflictException(
    message:String?=null,
    cause:Throwable?=null,
    enableSuppression:Boolean=false,
    writableStackTrace:Boolean=false,
) : RuntimeException(message,cause,enableSuppression,writableStackTrace)