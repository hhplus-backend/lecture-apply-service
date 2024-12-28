package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.domain.lecture.extension.toResponse
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
) {
    @Transactional(readOnly = true)
    fun getAvailableLectures(
        request: LectureSearchRequest,
        pageable: Pageable,
    ): Page<LectureResponse> {
        val lectures = lectureRepository.findAvailableLecturesByDate(request, pageable)
        return lectures.map { it.toResponse() }
    }
}
