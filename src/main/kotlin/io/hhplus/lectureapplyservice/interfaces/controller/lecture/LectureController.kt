package io.hhplus.lectureapplyservice.interfaces.controller.lecture

import io.hhplus.lectureapplyservice.application.dto.request.LectureApplyRequest
import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.application.facade.LectureFacade
import io.hhplus.lectureapplyservice.common.response.ApiResponse
import io.hhplus.lectureapplyservice.common.response.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lectures")
class LectureController(
    private val lectureFacade: LectureFacade,
) {
    @PostMapping("/{lectureId}/apply")
    @Operation(summary = "강의 신청")
    fun applyForLecture(
        @PathVariable lectureId: Long,
        @RequestBody request: LectureApplyRequest,
    ): ResponseEntity<SuccessResponse<Boolean>> {
        val isSuccess = lectureFacade.applyForLecture(lectureId, request.userId)
        return ApiResponse.success(isSuccess)
    }

    @GetMapping("")
    @Operation(summary = "userId의 신청 가능한 강의 조회")
    fun getAvailableLectures(
        @ModelAttribute request: LectureSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<SuccessResponse<Page<LectureResponse>>> {
        val lectures = lectureFacade.getAvailableLectures(request, pageable)
        return ApiResponse.success(lectures)
    }

    @GetMapping("/applied")
    @Operation(summary = "userId의 강의 신청 내역 조회")
    fun getAppliedLectures(
        @RequestParam userId: Long,
        pageable: Pageable,
    ): ResponseEntity<SuccessResponse<Page<LectureResponse>>> {
        val lectures = lectureFacade.getAppliedLectures(userId, pageable)
        return ApiResponse.success(lectures)
    }
}
