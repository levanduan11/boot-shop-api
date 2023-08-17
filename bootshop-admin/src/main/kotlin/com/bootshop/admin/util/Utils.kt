package com.bootshop.admin.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.util.Optional



fun isBeforeTime(timeCheck: LocalDateTime, timeCurrent: LocalDateTime): Boolean {
    return timeCheck.isBefore(timeCurrent)
}

fun isAfterTime(timeCheck: LocalDateTime, timeCurrent: LocalDateTime): Boolean {
    return timeCheck.isAfter(timeCurrent)
}

fun main() {
//    val created=LocalDateTime.of(LocalDate.now(), LocalTime.of(12,0));
//    val validThresold=created.plusHours(1)
//    val now = LocalDateTime.of(LocalDate.now(),LocalTime.of(13,10))
//    val diff = Duration.between(now,validThresold)
//    val res =diff.toMinutes()
    val a:String? =null
    a?.let {
        println("abc")
    }

}