package com.bootshop.admin.web.res

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse (
    var status:Boolean=false,
    var httpStatus: Int?=null,
    var message:String?=null,
    var data:Any?=null,
    var errors:Any?=null
)