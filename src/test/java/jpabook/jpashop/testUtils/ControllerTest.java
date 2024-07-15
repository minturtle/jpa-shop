package jpabook.jpashop.testUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestFixtureConfig.class)
public abstract class ControllerTest {

    @Autowired
    protected TestDataFixture testDataFixture;

    @SpyBean
    protected PasswordEncoder passwordEncoder;


    @AfterEach
    void tearDown() {
        testDataFixture.deleteAll();
    }
}
