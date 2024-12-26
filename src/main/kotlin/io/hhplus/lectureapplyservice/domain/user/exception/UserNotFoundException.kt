package io.hhplus.lectureapplyservice.domain.user.exception

class UserNotFoundException(
    val userId: Long,
    cause: Throwable? = null,
) : UserException("userId: $userId is not found", cause)
