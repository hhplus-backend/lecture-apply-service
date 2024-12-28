package io.hhplus.lectureapplyservice.domain.lecture.repository

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureApplicationRepository {
    fun save(lectureApplication: LectureApplication): LectureApplication

    fun getAppliedLectures(
        userId: Long,
        pageable: Pageable,
    ): Page<Lecture>

    fun deleteAll()
}
