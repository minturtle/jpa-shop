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

    @Test
    @DisplayName("비밀번호를 암호화 할 수 있다. 이 때 암호화 함수는 멱등성을 보장한다.")
    public void testEncodePassword() throws Exception{
        //given
        String givenPassword = "givenPassword";
        byte[] givenSalt = passwordUtils.createSalt();

        //when
        String actual1 = passwordUtils.encodePassword(givenPassword, givenSalt);
        String actual2 = passwordUtils.encodePassword(givenPassword, givenSalt);

        //then
        assertThat(actual1).isNotEqualTo(givenPassword);

        assertThat(actual1).isEqualTo(actual2);
    }

    @Test
    @DisplayName("비밀번호 원문과 salt 값을 통해 암호화된 비밀번호와 비교할 수 있다.")
    public void testMatchPassword() throws Exception{
        //given
        String givenPassword = "givenPassword";
        byte[] givenSalt = passwordUtils.createSalt();
        byte[] givenSalt2 = passwordUtils.createSalt();

        String encodedPassword = passwordUtils.encodePassword(givenPassword, givenSalt);
        //when
        boolean expectedTrue = passwordUtils.matches(givenPassword, givenSalt, encodedPassword);
        boolean expectedFalse = passwordUtils.matches(givenPassword, givenSalt2, encodedPassword);

        //then
        assertThat(expectedTrue).isTrue();
        assertThat(expectedFalse).isFalse();
    }






    @TestConfiguration
    public static class TestConfig{

        @Bean
        public PasswordUtils passwordUtils(){
            return new PasswordUtils();
        }


    }

}