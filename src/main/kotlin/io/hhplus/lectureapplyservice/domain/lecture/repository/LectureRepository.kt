package io.hhplus.lectureapplyservice.domain.lecture.repository

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepository {
    fun getLock(key: String): Int

    fun releaseLock(key: String): Int

    fun getLectureById(lectureId: Long): Lecture

    fun getLectureByIdWithLock(lectureId: Long): Lecture

    fun findAvailableLecturesByDate(
        request: LectureSearchRequest,
        pageable: Pageable,
    ): Page<Lecture>

    fun save(lecture: Lecture): Lecture

    fun saveAll(lectureList: List<Lecture>): List<Lecture>

    fun deleteAll()
}
