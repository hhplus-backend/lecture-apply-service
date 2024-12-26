package io.hhplus.lectureapplyservice.domain.lecture.exception

class LectureNotFoundException(
    val lectureId: Long,
    cause: Throwable? = null,
) : LectureException("lectureId: $lectureId is not found", cause)
