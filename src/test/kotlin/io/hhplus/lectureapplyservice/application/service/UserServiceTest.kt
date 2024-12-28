package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.exception.UserNotFoundException
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk

class UserServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val userService = UserService(userRepository)

    given("존재하는 userid가 주어지면") {
        val userId = 1L
        val expectedUser = User(userId, "test user")
        every { userRepository.getUserById(any()) } returns expectedUser

        `when`("getUser 호출될때") {
            val result = userService.getUser(userId)

            then("결과로 user가 반환된다.") {
                result shouldBe expectedUser
            }
        }
    }

    given("존재하지 않는 userid가 주어지면") {
        val userId = 999L
        every { userRepository.getUserById(userId) } throws UserNotFoundException(userId)

        `when`("getUser 호출될때") {
            val exception =
                shouldThrow<UserNotFoundException> {
                    userService.getUser(userId)
                }
            exception.message shouldContain "userId: $userId is not found"
        }
    }
})
