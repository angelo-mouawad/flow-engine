package be.ucll;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("dev")
public class TestConfig {
    // This class ensures tests run with the test profile
}
