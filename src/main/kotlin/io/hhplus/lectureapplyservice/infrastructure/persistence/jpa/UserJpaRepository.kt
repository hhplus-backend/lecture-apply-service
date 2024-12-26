package io.hhplus.lectureapplyservice.infrastructure.persistence.jpa

import io.hhplus.lectureapplyservice.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long>
