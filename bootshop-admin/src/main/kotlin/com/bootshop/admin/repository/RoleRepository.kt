package com.bootshop.admin.repository

import com.bootshop.common.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role,Int>{
    fun findByName(name:String):Role?
}