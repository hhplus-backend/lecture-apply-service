package io.hhplus.lectureapplyservice.infrastructure.persistence

import io.hhplus.lectureapplyservice.domain.user.User
import io.hhplus.lectureapplyservice.domain.user.exception.UserNotFoundException
import io.hhplus.lectureapplyservice.domain.user.repository.UserRepository
import io.hhplus.lectureapplyservice.infrastructure.persistence.jpa.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun saveAll(user: List<User>): List<User> {
        return userJpaRepository.saveAll(user)
    }

    override fun getUserById(userId: Long): User {
        return userJpaRepository.findById(userId).orElseThrow {
            UserNotFoundException(userId)
        }
    }

    override fun deleteAll() {
        userJpaRepository.deleteAll()
    }
}
