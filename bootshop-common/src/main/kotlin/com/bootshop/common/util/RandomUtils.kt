package com.bootshop.common.util

import java.security.SecureRandom
import kotlin.random.Random

class RandomUtils {
    companion object {
        fun randomString(len: Int): String {
            val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..len)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        fun randomToken(len: Int): String {
            val str = generateChar()
            val ran = SecureRandom()
            val res = StringBuilder()
            for (i in 0 until len) {
                val index = ran.nextInt(str.length)
                res.append(str[index])
            }
            return res.toString()
        }

        private fun generateChar(): String {
            val sb = StringBuilder()
            (0 until 26).forEach {
                if (it < 10) {
                    sb.append('0' + it)
                }
                sb.append('a' + it)
                sb.append('A' + it)
            }
            return sb.toString()
        }
    }
}
