package io.hhplus.lectureapplyservice

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<LectureApplyServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}