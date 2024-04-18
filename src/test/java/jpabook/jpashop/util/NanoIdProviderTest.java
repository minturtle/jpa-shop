package jpabook.jpashop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class NanoIdProviderTest {

    @Autowired
    private NanoIdProvider nanoIdProvider;

    @Value("${nanoId.size}")
    private int propertySize;

    @ParameterizedTest
    @CsvSource(value = {"10", "20", "30"})
    @DisplayName("nanoId의 크기를 입력받아 생성 할 수 있다")
    public void testCreateNanoIdWithSize(int givenSize) throws Exception{
        //given
        //when
        String actual = nanoIdProvider.createNanoId(givenSize);
        //then
        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(givenSize);

    }


    @Test
    @DisplayName("property로 설정된 크기로 nanoId를 생성할 수 있다.")
    public void testCreateNanoIdWithPropertySize() throws Exception{
        //given

        //when
        String actual = nanoIdProvider.createNanoId();
        //then
        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(propertySize);
    }


    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NanoIdProvider nanoIdProvider(){
            return new NanoIdProvider();
        }

    }
}