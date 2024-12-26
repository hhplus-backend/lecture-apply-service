package io.hhplus.lectureapplyservice.infrastructure.persistence.jpa

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface LectureJpaRepository : JpaRepository<Lecture, Long> {

    @Query(value = "SELECT GET_LOCK(:key, 3000)", nativeQuery = true)
    fun getLock(key: String): Int

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    fun releaseLock(key: String): Int

    @Query(
        """
        SELECT l FROM Lecture l
        WHERE l.id = :lectureId FOR UPDATE
        """, nativeQuery = true
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락 적용
    fun findLectureWithLock(@Param("lectureId") lectureId: Long): Optional<Lecture>

    @Query(
        """
    SELECT l
    FROM Lecture l
    WHERE (:#{#request.date} IS NULL OR l.lectureDate = :#{#request.date})
      AND l.capacity > (
          SELECT COUNT(la)
          FROM LectureApplication la
          WHERE la.lecture = l
      )
      AND (:#{#request.userId} IS NULL OR l.id NOT IN (
          SELECT la.lecture.id
          FROM LectureApplication la
          WHERE la.participant.id = :#{#request.userId}
      ))
    """
    )
    fun findAvailableLecturesByDate(@Param("request") request: LectureSearchRequest, pageable: Pageable): Page<Lecture>
}
