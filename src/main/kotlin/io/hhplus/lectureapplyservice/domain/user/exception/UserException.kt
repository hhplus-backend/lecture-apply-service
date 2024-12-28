package io.hhplus.lectureapplyservice.domain.user.exception

import io.hhplus.lectureapplyservice.common.exception.ServiceException

open class UserException(
    message: String = "user exception",
    cause: Throwable? = null,
) : ServiceException(message, cause)
