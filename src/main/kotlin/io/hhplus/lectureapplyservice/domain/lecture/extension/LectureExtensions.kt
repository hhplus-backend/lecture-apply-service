package io.hhplus.lectureapplyservice.domain.lecture.extension

import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.domain.lecture.Lecture

fun Lecture.toResponse(): LectureResponse {
    return LectureResponse(
        id = this.id,
        name = this.name,
        lecturer = this.lecturer,
        lectureDate = this.lectureDate,
        capacity = this.capacity,
        remaining = this.capacity - lectureApplications.size,
    )
}
