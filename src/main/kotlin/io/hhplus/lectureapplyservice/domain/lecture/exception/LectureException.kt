package io.hhplus.lectureapplyservice.domain.lecture.exception

import io.hhplus.lectureapplyservice.common.exception.ServiceException

open class LectureException(
    message: String = "lecture exception",
    cause: Throwable? = null,
) : ServiceException(message, cause)
