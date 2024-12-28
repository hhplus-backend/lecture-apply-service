package io.hhplus.lectureapplyservice

import io.hhplus.lectureapplyservice.domain.lecture.Lecture
import io.hhplus.lectureapplyservice.domain.lecture.repository.LectureRepository
import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Profile("!integration-test")
class InitializeDataLoader(
    private val userRepository: UserRepository,
    private val lectureRepository: LectureRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val users = mutableListOf<User>()
        for (i in 1..100) {
            users.add(
                User(
                    id = i.toLong(),
                    name = "test$i",
                ),
            )
        }

        userRepository.saveAll(users)

        val lectures = mutableListOf<Lecture>()
        for (i in 1..3) {
            val date = LocalDate.now().minusDays(i.toLong())
            lectures.add(
                Lecture(
                    id = i.toLong(),
                    name = "lecture-$i",
                    lecturer = "lecturer-$i",
                    lectureDate = date,
                ),
            )
        }

        lectureRepository.saveAll(lectures)
    }
}
