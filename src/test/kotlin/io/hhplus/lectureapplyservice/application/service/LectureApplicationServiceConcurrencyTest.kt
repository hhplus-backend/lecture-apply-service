package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureApplicationRepository
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("integration-test")
class LectureApplicationServiceConcurrencyTest
    @Autowired
    constructor(
        private val lectureApplicationRepository: LectureApplicationRepository,
        private val lectureRepository: LectureRepository,
        private val userRepository: UserRepository,
        private val lectureApplicationService: LectureApplicationService,
    ) : BehaviorSpec({

            val clock = Clock.systemDefaultZone()
            val currentDate = LocalDate.now(clock)

            afterEach {
                lectureRepository.deleteAll()
                userRepository.deleteAll()
            }

            given("40명이 동시에 같은 강의에 요청하였을 때") {
                val lectureId = 1L
                val savedLecture =
                    lectureRepository.save(
                        Lecture(
                            id = lectureId,
                            name = "test lecture",
                            lecturer = "teacher",
                            lectureDate = currentDate,
                        ),
                    )

                val savedUser =
                    userRepository.save(
                        User(id = 1L, name = "Test User"),
                    )
                val successfulApplications = AtomicInteger(0)

                `when`("applyForLecture를 호출될때") {
                    runBlocking {
                        (1..40).map {
                            withContext(Dispatchers.IO) {
                                val result = lectureApplicationService.applyForLecture(savedLecture.id, savedUser)
                                if (result) successfulApplications.incrementAndGet()
                            }
                        }
                    }
                }

                then("30명만 성공한다.") {
                    successfulApplications.get() shouldBe 30
                }
            }
        })
