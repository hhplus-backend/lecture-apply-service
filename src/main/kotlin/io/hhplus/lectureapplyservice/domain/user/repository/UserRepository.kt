package io.hhplus.lectureapplyservice.domain.user.repository

import io.hhplus.lectureapplyservice.domain.user.User

interface UserRepository {
    fun save(user: User): User

    fun saveAll(user: List<User>): List<User>

    fun getUserById(userId: Long): User

    fun deleteAll()
}
