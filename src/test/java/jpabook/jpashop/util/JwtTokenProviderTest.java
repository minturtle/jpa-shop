package jpabook.jpashop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;





class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenProviderTest() {
        jwtTokenProvider = new JwtTokenProvider("secretKey", 1000000L);
    }

    @Test
    @DisplayName("JWT Token을 생성할 수 있다.")
    public void createJwtToken() throws Exception{
        //given
        String givenUid = "givenUid";
        Date createTime = new Date();

        //when
        String token = jwtTokenProvider.sign(givenUid, createTime);

        //then
        assertThat(token).isNotNull();
    }


}