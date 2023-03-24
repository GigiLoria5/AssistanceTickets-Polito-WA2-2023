package it.polito.wa2.g29.server.integration

import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractTestcontainersTest {

    companion object {
        @Container
        private val container = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .apply {
                this.withDatabaseName("testDb").withUsername("root").withPassword("123456")
            }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }

        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            container.start()
            println("Container Started")
        }
    }

}