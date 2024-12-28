package io.hhplus.lectureapplyservice.domain.user

import io.hhplus.lectureapplyservice.common.BaseEntity
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@Entity
@Table(name = "users")
@Comment("사용자")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    // val id: Long? = null, // id를 nullable로 변경
    @Column(nullable = false, unique = true)
    @Comment("사용자 이름")
    val name: String,
    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lectureApplications: List<LectureApplication> = mutableListOf(),
) : BaseEntity()
