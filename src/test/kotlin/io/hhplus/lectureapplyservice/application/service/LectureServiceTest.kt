package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.config.ClockConfig
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.extension.toResponse
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
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
class LectureServiceTest(
    @Autowired val clock: Clock,
) : BehaviorSpec({

        val lectureRepository = mockk<LectureRepository>()
        val lectureService = LectureService(lectureRepository)
        val fixedInstant = Instant.parse("2023-12-24T12:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of(ZoneOffset.UTC.id))

        given("유효한 searchrequest와 page가 주어지면") {
            val request =
                LectureSearchRequest(
                    userId = 1L,
                    date = LocalDate.now(fixedClock),
                )
            val pageable: Pageable = PageRequest.of(0, 10)
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
            every { lectureRepository.findAvailableLecturesByDate(any(), any()) } returns lecturePage

            `when`("getAvailableLectures가 호출될때") {
                val result = lectureService.getAvailableLectures(request, pageable)

                then("결과로 수강 가능한 강의목록을 page로 반환한다.") {
                    val expectedLectureResponses = lectures.map { it.toResponse() }
                    result.content shouldBe expectedLectureResponses
                }
            }
        }

        given("searchrequst의 존재하지 않는 userId가 주어지면") {
            val request =
                LectureSearchRequest(
                    userId = 999L,
                    date = LocalDate.now(fixedClock),
                )
            val pageable: Pageable = PageRequest.of(0, 10)
            val lectures = emptyList<Lecture>()
            val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
            every { lectureRepository.findAvailableLecturesByDate(any(), any()) } returns lecturePage

            `when`("getAvailableLectures가 호출될때 ") {
                val result = lectureService.getAvailableLectures(request, pageable)

                then("결과로 빈 목록을 page로 반환한다.") {
                    result.content shouldBe emptyList<LectureResponse>() // Expected an empty list of LectureResponse
                }
            }
        }

        given("searchrequst의 userid가 없다면") {
            val request =
                LectureSearchRequest(
                    date = LocalDate.now(fixedClock),
                )
            val pageable: Pageable = PageRequest.of(0, 10)
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
            every { lectureRepository.findAvailableLecturesByDate(any(), any()) } returns lecturePage

            `when`("getAvailableLectures가 호출될때 ") {
                val result = lectureService.getAvailableLectures(request, pageable)

                then("날짜에 맞는 수강 가능한 강의목록을 page로 반환한다.") {
                    val expectedLectureResponses = lectures.map { it.toResponse() }
                    result.content shouldBe expectedLectureResponses
                }
            }
        }

        given("searchrequst의 date가 없다면") {
            val request =
                LectureSearchRequest(
                    userId = 1L,
                )
            val pageable: Pageable = PageRequest.of(0, 10)
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
                        lectureDate = LocalDate.now(fixedClock).minusDays(1),
                    ),
                )
            val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
            every { lectureRepository.findAvailableLecturesByDate(any(), any()) } returns lecturePage

            `when`("getAvailableLectures가 호출될때 ") {
                val result = lectureService.getAvailableLectures(request, pageable)

                then("해당 사용자에 맞는 수강 가능한 강의목록을 page로 반환한다.") {
                    val expectedLectureResponses = lectures.map { it.toResponse() }
                    result.content shouldBe expectedLectureResponses
                }
            }
        }
    })
