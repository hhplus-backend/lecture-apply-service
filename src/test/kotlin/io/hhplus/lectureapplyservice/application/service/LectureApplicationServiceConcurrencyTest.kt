package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureAlreadyAppliedException
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureFullException
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureApplicationRepository
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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

            given("서로 다른 40명이 동시에 같은 강의에 요청하였을 때") {
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

                val users = mutableListOf<User>()
                for (i in 1..40) {
                    users.add(
                        User(
                            name = "test$i",
                        ),
                    )
                }

                val savedUsers = userRepository.saveAll(users)

                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                `when`("applyForLecture를 호출될때") {
                    runBlocking {
                        (0..39).map { index ->
                            async(Dispatchers.IO) {
                                val result =
                                    runCatching {
                                        lectureApplicationService.applyForLecture(savedLecture.id, savedUsers[index])
                                    }

                                result.onSuccess {
                                    successfulApplications.incrementAndGet()
                                }.onFailure { e ->
                                    when (e) {
                                        is LectureFullException -> failApplications.incrementAndGet()
                                        else -> throw e // 다른 예외 처리
                                    }
                                }
                            }
                        }.forEach { it.await() }
                    }
                }

                then("30명만 성공한다.") {
                    successfulApplications.get() shouldBe 30
                    failApplications.get() shouldBe 10
                }
            }

            given("동일한 유저가 같은 특강을 5번 신청하였을 때") {
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
                        User(
                            name = "test-user",
                        ),
                    )

                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                `when`("applyForLecture를 호출될때") {
                    runBlocking {
                        (0..4).map {
                            async(Dispatchers.IO) {
                                val result =
                                    runCatching {
                                        lectureApplicationService.applyForLecture(savedLecture.id, savedUser)
                                    }

                                result.onSuccess {
                                    successfulApplications.incrementAndGet()
                                }.onFailure { e ->
                                    when (e) {
                                        is LectureAlreadyAppliedException -> failApplications.incrementAndGet()
                                        else -> throw e // 다른 예외 처리
                                    }
                                }
                            }
                        }.forEach { it.await() }
                    }
                }

                then("1번만 성공한다.") {
                    successfulApplications.get() shouldBe 1
                    failApplications.get() shouldBe 4
                }
            }
        })
