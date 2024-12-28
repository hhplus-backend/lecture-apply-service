package io.hhplus.lectureapplyservice.domain.lecture

import io.hhplus.lectureapplyservice.common.BaseEntity
import io.hhplus.lectureapplyservice.domain.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
@Table(name = "lectures")
@Comment("강의")
class Lecture(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(nullable = false)
    @Comment("강의명")
    val name: String,
    @Column(nullable = false)
    @Comment("강의자")
    val lecturer: String,
    @Column(nullable = false)
    @Comment("강의일")
    val lectureDate: LocalDate,
    @Column(nullable = false)
    @Comment("강의최대정원")
    val capacity: Int = MAX_CAPACITY,
    @OneToMany(mappedBy = "lecture", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lectureApplications: List<LectureApplication> = mutableListOf(),
) : BaseEntity() {
    companion object {
        const val MAX_CAPACITY = 30
    }

    fun hasCapacity(): Boolean {
        return getRemaining() > 0
    }

    fun getRemaining(): Int {
        return capacity - lectureApplications.size
    }

    fun hasUserApplied(user: User): Boolean {
        return lectureApplications.any { it.participant.id == user.id }
    }
}
