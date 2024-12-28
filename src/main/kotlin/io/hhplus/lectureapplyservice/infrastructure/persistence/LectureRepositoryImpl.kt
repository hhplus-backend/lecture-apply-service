package io.hhplus.lectureapplyservice.infrastructure.persistence

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureNotFoundException
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.infrastructure.persistence.jpa.LectureJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class LectureRepositoryImpl(
    private val lectureJpaRepository: LectureJpaRepository,
) : LectureRepository {
    override fun getLock(key: String): Int {
        return lectureJpaRepository.getLock(key)
    }

    override fun releaseLock(key: String): Int {
        return lectureJpaRepository.releaseLock(key)
    }

    override fun getLectureById(lectureId: Long): Lecture {
        return lectureJpaRepository.findById(lectureId).orElseThrow {
            throw LectureNotFoundException(lectureId)
        }
    }

    override fun getLectureByIdWithLock(lectureId: Long): Lecture {
        return lectureJpaRepository.findLectureWithLock(lectureId).orElseThrow {
            throw LectureNotFoundException(lectureId)
        }
    }

    override fun findAvailableLecturesByDate(
        request: LectureSearchRequest,
        pageable: Pageable,
    ): Page<Lecture> {
        return lectureJpaRepository.findAvailableLecturesByDate(request, pageable)
    }

    override fun save(lecture: Lecture): Lecture {
        return lectureJpaRepository.save(lecture)
    }

    override fun saveAll(lectureList: List<Lecture>): List<Lecture> {
        return lectureJpaRepository.saveAll(lectureList)
    }

    override fun deleteAll() {
        lectureJpaRepository.deleteAll()
    }
}
