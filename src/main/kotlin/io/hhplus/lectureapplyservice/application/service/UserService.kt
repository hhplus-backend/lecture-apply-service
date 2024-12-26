package io.hhplus.lectureapplyservice.application.service

import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    fun getUser(userId: Long): User {
        return userRepository.getUserById(userId)
    }
}
