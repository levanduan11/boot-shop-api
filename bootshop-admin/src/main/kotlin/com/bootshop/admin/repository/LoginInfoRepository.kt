package com.bootshop.admin.repository

import com.bootshop.common.model.LoginInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface LoginInfoRepository : JpaRepository<LoginInfo,Int>{

    @Query("SELECT COUNT(*) FROM login_info WHERE user_id = ?1 AND success = TRUE", nativeQuery = true)
    fun countLoginSuccessByUserId(userId:Int):Long
    @Query("SELECT COUNT(*) FROM login_info WHERE user_id = ?1 AND reset_first_time = TRUE AND success = TRUE", nativeQuery = true)
    fun countResetFirstTime(userId: Int):Long
    fun findByUserIdAndResetFirstTimeIsTrue(userId: Int):Optional<LoginInfo>
}