package io.hhplus.lectureapplyservice.application.dto.response

import java.time.LocalDate

data class LectureResponse(
    val id: Long,
    val name: String,
    val lecturer: String,
    val lectureDate: LocalDate,
    val capacity: Int,
    val remaining: Int,
)
