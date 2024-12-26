package io.hhplus.lectureapplyservice.domain.lecture

import io.hhplus.lectureapplyservice.config.ClockConfig
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureFullException
import io.hhplus.lectureapplyservice.domain.user.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Import(ClockConfig::class)
class LectureTest(
    @Autowired val clock: Clock,
) : FunSpec({

        lateinit var lecture: Lecture

        beforeTest {
            val fixedInstant = Instant.parse("2023-12-24T12:00:00Z")
            val fixedClock = Clock.fixed(fixedInstant, ZoneId.of(ZoneOffset.UTC.id))

            lecture =
                Lecture(
                    name = "lecture-1",
                    lecturer = "lecturer-1",
                    lectureDate = LocalDate.now(fixedClock),
                )
        }

        test("강의최대 정원까지 모두 찬 경우 getRemaining은 0을 리턴한다") {
            val randomUsers =
                List(Lecture.MAX_CAPACITY) { index ->
                    User(
                        id = index.toLong(),
                        name = "User${index + 1}",
                    )
                }

            val applications =
                randomUsers.map { user ->
                    LectureApplication(
                        lecture = lecture,
                        participant = user,
                    )
                }
            (lecture.lectureApplications as MutableList).addAll(applications)

            lecture.getRemaining() shouldBe 0
        }

        test("getRemaining은 강의의 잔여 좌석을 리턴한다.") {
            val appliedCount = Lecture.MAX_CAPACITY - 5
            val randomUsers =
                List(appliedCount) { index ->
                    User(
                        id = index.toLong(),
                        name = "User${index + 1}",
                    )
                }

            val applications =
                randomUsers.map { user ->
                    LectureApplication(
                        lecture = lecture,
                        participant = user,
                    )
                }
            (lecture.lectureApplications as MutableList).addAll(applications)

            lecture.getRemaining() shouldBe 5
        }

        test("hasCapacity는 강의의 잔여 좌석이 있다면 true를 리턴한다.") {
            val appliedCount = Lecture.MAX_CAPACITY - 5
            val randomUsers =
                List(appliedCount) { index ->
                    User(
                        id = index.toLong(),
                        name = "User${index + 1}",
                    )
                }

            val applications =
                randomUsers.map { user ->
                    LectureApplication(
                        lecture = lecture,
                        participant = user,
                    )
                }
            (lecture.lectureApplications as MutableList).addAll(applications)

            lecture.hasCapacity() shouldBe true
        }

        test("hasCapacity는 강의의 잔여 좌석이 0이하라면 LectureFullException를 발생시킨다.") {
            val appliedCount = Lecture.MAX_CAPACITY
            val randomUsers =
                List(appliedCount) { index ->
                    User(
                        id = index.toLong(),
                        name = "User${index + 1}",
                    )
                }

            val applications =
                randomUsers.map { user ->
                    LectureApplication(
                        lecture = lecture,
                        participant = user,
                    )
                }
            (lecture.lectureApplications as MutableList).addAll(applications)

            val exception =
                shouldThrow<LectureFullException> {
                    lecture.hasCapacity()
                }
        }
    })
