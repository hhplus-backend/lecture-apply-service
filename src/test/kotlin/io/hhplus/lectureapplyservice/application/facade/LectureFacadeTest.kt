package io.hhplus.lectureapplyservice.application.facade

import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.domain.lecture.Lecture
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("integration-test")
class LectureFacadeTest
    @Autowired
    constructor(
        private val lectureFacade: LectureFacade,
        private val userRepository: UserRepository,
        private val lectureRepository: LectureRepository,
    ) : BehaviorSpec({

            val fixedInstant = Instant.parse("2023-12-24T12:00:00Z")
            val fixedClock = Clock.fixed(fixedInstant, ZoneId.of(ZoneOffset.UTC.id))

            afterEach {
                lectureRepository.deleteAll()
                userRepository.deleteAll()
            }

            given("하나의 강의, 하나의 user가 주어질때") {
                val savedUser = userRepository.save(User(id = 1L, name = "test"))
                val savedLecture =
                    lectureRepository.save(
                        Lecture(
                            id = 1L,
                            name = "lecture-1",
                            lecturer = "lecturer-1",
                            lectureDate = LocalDate.now(fixedClock),
                        ),
                    )

                `when`("수강신청을 진행하면") {
                    val result = lectureFacade.applyForLecture(savedUser.id, savedLecture.id)
                    then("true를 반환한다.") {
                        result shouldBe true
                    }
                }
            }

            given("하나의 강의에 30명의 user가 주어질때") {
                val users = mutableListOf<User>()
                for (i in 1..30) {
                    users.add(
                        User(
//                    id = i.toLong(),
                            name = "test$i",
                        ),
                    )
                }

                val savedUsers = userRepository.saveAll(users)

                val savedLecture =
                    lectureRepository.save(
                        Lecture(
                            id = 1L,
                            name = "lecture-1",
                            lecturer = "lecturer-1",
                            lectureDate = LocalDate.now(fixedClock),
                        ),
                    )

                val successfulApplications = AtomicInteger(0)

                `when`("수강신청을 진행하면") {
                    runBlocking {
                        (0..29).map { index ->
                            withContext(Dispatchers.IO) {
                                val result = lectureFacade.applyForLecture(savedLecture.id, savedUsers[index].id)
                                if (result) successfulApplications.incrementAndGet()
                            }
                        }
                    }
                    then("30명 모두 성공한다.") {
                        successfulApplications.get() shouldBe 30
                    }
                }
            }

            given("하나의 강의에 40명의 user가 주어질때") {
                val users = mutableListOf<User>()
                for (i in 1..40) {
                    users.add(
                        User(
                            id = i.toLong(),
                            name = "test$i",
                        ),
                    )
                }

                val savedUsers = userRepository.saveAll(users)

                val savedLecture =
                    lectureRepository.save(
                        Lecture(
                            id = 1L,
                            name = "lecture-1",
                            lecturer = "lecturer-1",
                            lectureDate = LocalDate.now(fixedClock),
                        ),
                    )

                val successfulApplications = AtomicInteger(0)

                `when`("수강신청을 진행하면") {
                    runBlocking {
                        (0..39).map { index ->
                            withContext(Dispatchers.IO) {
                                val result = lectureFacade.applyForLecture(savedLecture.id, savedUsers[index].id)
                                if (result) successfulApplications.incrementAndGet()
                            }
                        }
                    }
                    then("30명만 성공한다.") {
                        successfulApplications.get() shouldBe 30
                    }
                }
            }

            given("동일한 유저가 같은 특강을 5번 신청하였을 때") {
                val savedUser =
                    userRepository.save(
                        User(
                            name = "test-user",
                        ),
                    )

                val savedLecture =
                    lectureRepository.save(
                        Lecture(
                            id = 1L,
                            name = "lecture-1",
                            lecturer = "lecturer-1",
                            lectureDate = LocalDate.now(fixedClock),
                        ),
                    )

                val successfulApplications = AtomicInteger(0)

                `when`("수강신청을 진행하면") {
                    runBlocking {
                        (0..39).map {
                            withContext(Dispatchers.IO) {
                                val result = lectureFacade.applyForLecture(savedLecture.id, savedUser.id)
                                if (result) successfulApplications.incrementAndGet()
                            }
                        }
                    }
                    then("1번만 성공한다.") {
                        successfulApplications.get() shouldBe 1
                    }
                }
            }

            given("userId, 날짜가 주어질때") {
                val savedUser = userRepository.save(User(id = 1L, name = "test"))

                val lectures = mutableListOf<Lecture>()
                for (i in 1..3) {
                    val date = LocalDate.now(fixedClock).minusDays(i.toLong())
                    lectures.add(
                        Lecture(
                            id = i.toLong(),
                            name = "lecture-$i",
                            lecturer = "lecturer-$i",
                            lectureDate = date,
                        ),
                    )
                }

                val savedLectures = lectureRepository.saveAll(lectures)

                val pageable: Pageable = PageRequest.of(0, 10)
                val request = LectureSearchRequest(savedUser.id, LocalDate.now(fixedClock).minusDays(1))
                `when`("수강 가능한 강의를 조회하면") {

                    val result = lectureFacade.getAvailableLectures(request, pageable)

                    then("수강 가능한 강의가 조회된다.") {
                        result.content.size shouldBe 1
                    }
                }
            }

            given("userId가 주어질때") {
                val savedUser = userRepository.save(User(id = 1L, name = "test"))

                val lectures = mutableListOf<Lecture>()
                for (i in 1..3) {
                    val date = LocalDate.now(fixedClock).minusDays(i.toLong())
                    lectures.add(
                        Lecture(
                            id = i.toLong(),
                            name = "lecture-$i",
                            lecturer = "lecturer-$i",
                            lectureDate = date,
                        ),
                    )
                }

                val savedLectures = lectureRepository.saveAll(lectures)

                val pageable: Pageable = PageRequest.of(0, 10)
                val request = LectureSearchRequest(savedUser.id)
                `when`("수강 가능한 강의를 조회하면") {

                    val result = lectureFacade.getAvailableLectures(request, pageable)

                    then("수강 가능한 강의가 조회된다.") {
                        result.content.size shouldBe 3
                    }
                }
            }

            given("날짜가 주어질때") {
                val savedUser = userRepository.save(User(id = 1L, name = "test"))

                val lectures = mutableListOf<Lecture>()
                for (i in 1..3) {
                    val date = LocalDate.now(fixedClock).minusDays(i.toLong())
                    lectures.add(
                        Lecture(
                            id = i.toLong(),
                            name = "lecture-$i",
                            lecturer = "lecturer-$i",
                            lectureDate = date,
                        ),
                    )
                }

                val savedLectures = lectureRepository.saveAll(lectures)

                val pageable: Pageable = PageRequest.of(0, 10)
                val request = LectureSearchRequest(date = LocalDate.now(fixedClock).minusDays(1))
                `when`("수강 가능한 강의를 조회하면") {

                    val result = lectureFacade.getAvailableLectures(request, pageable)

                    then("수강 가능한 강의가 조회된다.") {
                        result.content.size shouldBe 1
                    }
                }
            }
        })
