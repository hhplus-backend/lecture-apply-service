package io.hhplus.lectureapplyservice.domain.lecture.exception

class LectureAlreadyAppliedException(
    val userId: Long,
    val lectureId: Long,
    cause: Throwable? = null,
) : LectureException("userId: $userId has already applied lectureId: $lectureId", cause)
