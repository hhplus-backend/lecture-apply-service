package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.config.ClockConfig
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.LectureApplication
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureNotFoundException
import io.hhplus.lectureapplyservice.domain.lecture.extension.toResponse
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureApplicationRepository
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.domain.user.User
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Import(ClockConfig::class)
class LectureApplicationServiceTest(
    @Autowired val clock: Clock,
) : BehaviorSpec({

        val lectureRepository: LectureRepository = mockk()
        val lectureApplicationRepository: LectureApplicationRepository = mockk()
        val lectureApplicationService = LectureApplicationService(lectureApplicationRepository, lectureRepository)
        val fixedInstant = Instant.parse("2023-12-24T12:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of(ZoneOffset.UTC.id))

        val lectureId = 1L
        val user = User(id = 1L, name = "TestUser")
        val pageable: Pageable = PageRequest.of(0, 10)
        val lecture =
            Lecture(
                id = 1L,
                name = "lecture-1",
                lecturer = "lecturer-1",
                lectureDate = LocalDate.now(fixedClock),
            )
        val lockKey = "lecture_$lectureId"

        given("유효한 강의 ID와 사용자 정보가 주어지고, lock 획득에 성공하면") {
            every { lectureRepository.getLectureByIdWithLock(lectureId) } returns lecture
            every { lectureApplicationRepository.save(any()) } returns
                LectureApplication(
                    lecture = lecture,
                    participant = user,
                )

            `when`("applyForLecture 호출될때") {
                val result = lectureApplicationService.applyForLecture(lectureId, user)

                then("강의 신청이 성공적으로 완료되어야 한다.") {
                    result shouldBe true
                }
            }
        }

        given("유효한 강의 ID와 사용자 정보가 주어지고, 강의를 찾지못하여 lock 획득에 실패하면") {
            every { lectureRepository.getLectureByIdWithLock(lectureId) } throws LectureNotFoundException(lectureId)
            `when`("applyForLecture 호출될때") {
                val result =
                    runCatching {
                        lectureApplicationService.applyForLecture(lectureId, user)
                    }.isFailure

                then("강의 신청이 실패해야 한다.") {
                    result shouldBe true
                }
            }
        }

        given("유저 ID와 페이지 정보가 주어지면") {
            val lectures =
                listOf(
                    Lecture(
                        id = 1L,
                        name = "lecture-1",
                        lecturer = "lecturer-1",
                        lectureDate = LocalDate.now(fixedClock),
                    ),
                    Lecture(
                        id = 2L,
                        name = "lecture-2",
                        lecturer = "lecturer-2",
                        lectureDate = LocalDate.now(fixedClock),
                    ),
                )
            val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())

            every { lectureApplicationRepository.getAppliedLectures(any(), any()) } returns lecturePage

            `when`("getAppliedLectures 호출될때") {
                val result = lectureApplicationService.getAppliedLectures(user.id, pageable)

                then("유저가 신청한 강의 목록이 반환되어야 한다.") {
                    val expectedLectureResponses = lectures.map { it.toResponse() }
                    result.content shouldBe expectedLectureResponses
                }
            }
        }
    })
