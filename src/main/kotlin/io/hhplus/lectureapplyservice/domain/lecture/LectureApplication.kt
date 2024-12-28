package io.hhplus.lectureapplyservice.domain.lecture

import io.hhplus.lectureapplyservice.common.BaseEntity
import io.hhplus.lectureapplyservice.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment

@Entity
@Table(name = "lecture_applications", uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_id", "user_id"])])
@Comment("강의신청내역")
class LectureApplication(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    @Comment("강의 id")
    val lecture: Lecture,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자 id")
    val participant: User,
) : BaseEntity()
