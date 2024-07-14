package jpabook.jpashop.testUtils;

import jpabook.jpashop.util.NanoIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;



@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestFixtureConfig.class)
public abstract class ServiceTest {

    @SpyBean
    protected PasswordEncoder passwordEncoder;


    @SpyBean
    protected NanoIdProvider nanoIdProvider;

    @Autowired
    protected TestDataFixture testDataFixture;

}
