package io.hhplus.lectureapplyservice.common.exception

open class ServiceException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
