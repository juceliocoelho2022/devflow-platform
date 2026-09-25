package com.devflow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@org.springframework.test.context.ActiveProfiles("test")
@SpringBootTest(properties="app.jwt.secret=context-test-only-key-with-at-least-32-bytes") class DevFlowApplicationTests {@Test void contextLoads(){}}
