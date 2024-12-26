package io.hhplus.lectureapplyservice.interfaces.controller.lecture

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.hhplus.lectureapplyservice.application.dto.request.LectureApplyRequest
import io.hhplus.lectureapplyservice.application.dto.request.LectureSearchRequest
import io.hhplus.lectureapplyservice.application.dto.response.LectureResponse
import io.hhplus.lectureapplyservice.application.facade.LectureFacade
import io.hhplus.lectureapplyservice.common.response.ErrorCode
import io.hhplus.lectureapplyservice.common.response.SuccessCode
import io.hhplus.lectureapplyservice.domain.lecture.exception.LectureNotFoundException
import io.hhplus.lectureapplyservice.domain.user.exception.UserNotFoundException
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Clock
import java.time.LocalDate

@WebMvcTest(LectureController::class)
class LectureControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @MockkBean private val lectureFacade: LectureFacade,
) : DescribeSpec({

        val clock = Clock.systemDefaultZone()
        val mapper = jacksonObjectMapper()
        val baseUrl = "/api/lectures"

        describe("POST $baseUrl/{lectureId}/apply") {
            context("정상적인 강의 신청을 보냈을 때") {
                it("신청이 성공한다.") {
                    val lectureId = 1L
                    val userId = 123L
                    val request = LectureApplyRequest(userId = userId)

                    every { lectureFacade.applyForLecture(lectureId, userId) } returns true

                    mockMvc.post("$baseUrl/$lectureId/apply") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(request)
                    }.andExpect {
                        status { isOk() }
                        content {
                            jsonPath("$.code") { SuccessCode.SUCCESS.code }
                            jsonPath("$.message") { SuccessCode.SUCCESS.message }
                            jsonPath("$.timestamp") { isNotEmpty() }
                            jsonPath("$.data") { value(true) }
                        }
                    }
                }
            }

            context("유효하지 않은 userid를 요청 보냈을 때") {
                it("500 응답과 에러 메시지를 받는다.") {
                    val lectureId = 125L
                    val userId = -1L
                    val request = LectureApplyRequest(userId = userId)

                    every { lectureFacade.applyForLecture(lectureId, userId) } throws UserNotFoundException(userId)

                    mockMvc.post("$baseUrl/$lectureId/apply") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(request)
                    }.andExpect {
                        status { isInternalServerError() }
                        content {
                            jsonPath("$.code") { ErrorCode.INTERNAL_SERVER_ERROR.code }
                            jsonPath("$.message") { ErrorCode.INTERNAL_SERVER_ERROR.message }
                            jsonPath("$.timestamp") { isNotEmpty() }
                            jsonPath("$.data") { value("userId: $userId is not found") }
                        }
                    }
                }
            }

            context("유효하지 않은 lectureid를 요청 보냈을 때") {
                it("500 응답과 에러 메시지를 받는다.") {
                    val lectureId = -1L
                    val userId = 125L
                    val request = LectureApplyRequest(userId = userId)

                    every { lectureFacade.applyForLecture(lectureId, userId) } throws LectureNotFoundException(lectureId)

                    mockMvc.post("$baseUrl/$lectureId/apply") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(request)
                    }.andExpect {
                        status { isInternalServerError() }
                        content {
                            jsonPath("$.code") { ErrorCode.INTERNAL_SERVER_ERROR.code }
                            jsonPath("$.message") { ErrorCode.INTERNAL_SERVER_ERROR.message }
                            jsonPath("$.timestamp") { isNotEmpty() }
                            jsonPath("$.data") { value("lectureId: $lectureId is not found") }
                        }
                    }
                }
            }
        }

        describe("GET $baseUrl") {
            context("정상적인 요청 보냈을 때") {
                it("신청 가능한 강의가 리턴된다.") {
                    val pageable = PageRequest.of(0, 10)
                    val request = LectureSearchRequest(userId = 123L, date = null)
                    val lectures =
                        listOf(
                            LectureResponse(
                                id = 1L,
                                name = "Lecture 1",
                                lecturer = "teacher1",
                                lectureDate = LocalDate.of(2024, 12, 24),
                                capacity = 30,
                                remaining = 15,
                            ),
                            LectureResponse(
                                id = 2L,
                                name = "Lecture 2",
                                lecturer = "teacher1",
                                lectureDate = LocalDate.of(2024, 12, 25),
                                capacity = 25,
                                remaining = 15,
                            ),
                        )

                    val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
                    every { lectureFacade.getAvailableLectures(any(), any()) } returns lecturePage

                    mockMvc.get("$baseUrl") {
                        contentType = MediaType.APPLICATION_JSON
                        param("userId", request.userId.toString())
                    }.andExpect {
                        status { isOk() }
                        content {
                            jsonPath("$.code") { SuccessCode.SUCCESS.code }
                            jsonPath("$.message") { SuccessCode.SUCCESS.message }
                            jsonPath("$.timestamp") { isNotEmpty() }
                            jsonPath("$.data.content.length()") { value(2) }
                            jsonPath("$.data.content[0].name") { value("Lecture 1") }
                            jsonPath("$.data.content[1].name") { value("Lecture 2") }
                        }
                    }
                }

                it("신청 가능한 강의가 없다면 empty list를 리턴한다.") {
                    val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))
                    val request = LectureSearchRequest(userId = 123L, date = LocalDate.of(2024, 1, 1))
                    val lectures = emptyList<LectureResponse>()
                    val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
                    every { lectureFacade.getAvailableLectures(any(), any()) } returns lecturePage

                    mockMvc.get("$baseUrl") {
                        contentType = MediaType.APPLICATION_JSON
                        param("userId", request.userId.toString())
                    }.andExpect {
                        status { isOk() }
                        content {
                            jsonPath("$.data.content") { isArray() }
                            jsonPath("$.data.content.length()") { value(0) }
                        }
                    }
                }
            }

            context("userId를 빼고 요청을 보냈을 때") {
                it("400 에러를 반환한다.") {
                    val pageable = PageRequest.of(0, 10)
                    val request = LectureSearchRequest(userId = 123L, date = LocalDate.of(2024, 1, 1))
                    val lectures = emptyList<LectureResponse>()
                    val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
                    every { lectureFacade.getAvailableLectures(any(), any()) } returns lecturePage

                    mockMvc.get("$baseUrl/applied") {
                        contentType = MediaType.APPLICATION_JSON
                    }.andExpect {
                        status { isBadRequest() }
                    }
                }
            }
        }

        describe("GET /applied") {
            context("정상적인 요청 보냈을 때") {
                it("유저의 강의 신청 내역을 리턴한다.") {
                    val userId = 123L
                    val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))
                    val lectures =
                        listOf(
                            LectureResponse(
                                id = 3L,
                                name = "Lecture 3",
                                lecturer = "teacher3",
                                lectureDate = LocalDate.of(2024, 12, 24),
                                capacity = 25,
                                remaining = 15,
                            ),
                            LectureResponse(
                                id = 4L,
                                name = "Lecture 4",
                                lecturer = "teacher4",
                                lectureDate = LocalDate.of(2024, 12, 25),
                                capacity = 25,
                                remaining = 15,
                            ),
                        )
                    val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
                    every { lectureFacade.getAppliedLectures(any(), any()) } returns lecturePage

                    mockMvc.get("$baseUrl/applied") {
                        contentType = MediaType.APPLICATION_JSON
                        param("userId", userId.toString())
                    }.andExpect {
                        status { isOk() }
                        content {
                            jsonPath("$.data.content") { isArray() }
                            jsonPath("$.data.content.length()") { value(2) }
                            jsonPath("$.data.content[0].id") { value(3) }
                            jsonPath("$.data.content[1].id") { value(4) }
                        }
                    }
                }

                it("존재하지 않는 유저의 강의 신청 내역이 없으면 empty list를 리턴한다.") {
                    val userId = 123L
                    val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))
                    val lectures = emptyList<LectureResponse>()
                    val lecturePage = PageImpl(lectures, pageable, lectures.size.toLong())
                    every { lectureFacade.getAppliedLectures(any(), any()) } returns lecturePage

                    mockMvc.get("$baseUrl/applied") {
                        contentType = MediaType.APPLICATION_JSON
                        param("userId", userId.toString())
                    }.andExpect {
                        status { isOk() }
                        content {
                            jsonPath("$.data.content") { isArray() }
                            jsonPath("$.data.content.length()") { value(0) }
                        }
                    }
                }
            }
        }
    })
