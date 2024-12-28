package io.hhplus.lectureapplyservice.infrastructure.persistence

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureApplicationRepository
import io.hhplus.lectureapplyservice.infrastructure.persistence.jpa.LectureApplicationJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class LectureApplicationRepositoryImpl(
    private val lectureApplicationJpaRepository: LectureApplicationJpaRepository,
) : LectureApplicationRepository {
    override fun save(lectureApplication: LectureApplication): LectureApplication {
        return lectureApplicationJpaRepository.save(lectureApplication)
    }

    override fun getAppliedLectures(
        userId: Long,
        pageable: Pageable,
    ): Page<Lecture> {
        return lectureApplicationJpaRepository.findDistinctLecturesByParticipantId(userId, pageable)
    }

    override fun deleteAll() {
        lectureApplicationJpaRepository.deleteAll()
    }
}
