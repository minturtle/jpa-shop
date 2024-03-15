package jpabook.jpashop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
class PasswordUtilsTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Value("${spring.security.password.hashwidth}")
    private Integer expectedSaltSize;


    @Test
    @DisplayName("비밀번호 암호화를 위한 호출할 때 마다 다른 랜덤 문자열인 salt를 생성할 수 있다.")
    public void testGenerateSalt() throws Exception{
        //given

        //when
        byte[] salt1 = passwordUtils.createSalt();
        byte[] salt2 = passwordUtils.createSalt();
        //then
        assertThat(salt1).isNotNull();
        assertThat(salt2).isNotNull();

        assertThat(salt1).hasSize(expectedSaltSize);
        assertThat(salt2).hasSize(expectedSaltSize);

        assertThat(salt1).isNotEqualTo(salt2);

    }



    @TestConfiguration
    public static class TestConfig{

        @Bean
        public PasswordUtils passwordUtils(){
            return new PasswordUtils();
        }


    }

}