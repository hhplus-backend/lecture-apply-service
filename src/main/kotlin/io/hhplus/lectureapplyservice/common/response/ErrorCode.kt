package io.hhplus.lectureapplyservice.common.response

enum class ErrorCode(
    val code: String,
    val message: String,
) {
    FAIL("FAIL_01", "Fail"),

    USER_NOT_FOUND("USER_ERROR_01", "User not found"),
    LECTURE_FULL("LECTURE_01", "Lecture full"),
    INTERNAL_SERVER_ERROR("Internal server error", "Internal server error"),
}
