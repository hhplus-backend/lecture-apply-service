package io.hhplus.lectureapplyservice.application.dto.request

import java.time.LocalDate

data class LectureSearchRequest(
    val userId: Long? = null,
    val date: LocalDate? = null,
)
