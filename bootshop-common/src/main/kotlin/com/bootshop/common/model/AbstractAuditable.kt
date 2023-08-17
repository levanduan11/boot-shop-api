package com.bootshop.common.model

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.AbstractPersistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.lang.Nullable
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractAuditable<PK : Serializable>(

    @Nullable
    @CreatedBy
    protected var createdBy: String? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    @CreatedDate
    protected var createdDate: LocalDateTime? = null,

    @Nullable
    @LastModifiedBy
    protected var lastModifiedBy: String? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    @LastModifiedDate
    protected var lastModifiedDate: LocalDateTime? = null,

    ) : AbstractPersistable<PK>()

