package io.hhplus.lectureapplyservice.domain.lecture.exception

class LectureFullException(
    val lectureId: Long,
    cause: Throwable? = null,
) : LectureException("lectureId: $lectureId is full. not enough remaining", cause)
