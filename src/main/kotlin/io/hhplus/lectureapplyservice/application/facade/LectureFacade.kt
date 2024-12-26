package io.hhplus.lectureapplyservice.application.facade

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.application.service.LectureApplicationService
import io.hhplus.lectureapplyservice.application.service.LectureService
import io.hhplus.lectureapplyservice.application.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

// 사용자가 직관적으로 접근할 수 있도록 단순히 비즈니스 로직을 조합하는 역할만 수행.
// 비즈니스 로직이 있으면 안됨.
@Component
class LectureFacade(
    private val lectureService: LectureService,
    private val lectureApplicationService: LectureApplicationService,
    private val userService: UserService,
) {
    fun applyForLecture(
        lectureId: Long,
        userId: Long,
    ): Boolean {
        TODO()
    }

    fun getAvailableLectures(
        request: LectureSearchRequest,
        pageable: Pageable,
    ): Page<LectureResponse> {
        TODO()
    }

    fun getAppliedLectures(
        userId: Long,
        pageable: Pageable,
    ): Page<LectureResponse> {
        TODO()
    }
}
