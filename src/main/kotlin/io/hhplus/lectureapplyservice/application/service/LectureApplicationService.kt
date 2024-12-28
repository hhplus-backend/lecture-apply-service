package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureAlreadyAppliedException
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureFullException
import io.hhplus.lectureapplyservice.domain.lecture.extension.toResponse
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureApplicationRepository
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LectureApplicationService(
    private val lectureApplicationRepository: LectureApplicationRepository,
    private val lectureRepository: LectureRepository,
) {
    @Transactional
    fun applyForLecture(
        lectureId: Long,
        user: User,
    ): Boolean {
        val lecture = lectureRepository.getLectureByIdWithLock(lectureId)
        if (!lecture.hasCapacity()) {
            throw LectureFullException(lecture.id)
        }

        if (lecture.hasUserApplied(user)) {
            throw LectureAlreadyAppliedException(user.id, lecture.id)
        }

        val lectureApplication =
            LectureApplication(
                lecture = lecture,
                participant = user,
            )
        val savedLectureApplication = lectureApplicationRepository.save(lectureApplication)
        return true
    }

    fun getAppliedLectures(
        userId: Long,
        pageable: Pageable,
    ): Page<LectureResponse> {
        val lectureApplication = lectureApplicationRepository.getAppliedLectures(userId, pageable)
        return lectureApplication.map { it.toResponse() }
    }
}
