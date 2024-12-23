package io.hhplus.lectureapplyservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.utility.TestcontainersConfiguration

@Import(TestcontainersConfiguration::class)
@ActiveProfiles("integration-test")
@SpringBootTest
class LectureApplyServiceApplicationTests {
    @Test
    fun contextLoads() {
    }
}
