package io.hhplus.lectureapplyservice.infrastructure.persistence.jpa

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LectureApplicationJpaRepository : JpaRepository<LectureApplication, Long> {
    @Query("SELECT DISTINCT la.lecture FROM LectureApplication la WHERE la.participant.id = :userId")
    fun findDistinctLecturesByParticipantId(@Param("userId") userId: Long, pageable: Pageable): Page<Lecture>
}
