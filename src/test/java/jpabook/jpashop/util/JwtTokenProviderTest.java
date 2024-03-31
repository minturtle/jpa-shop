package jpabook.jpashop.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import org.assertj.core.api.ThrowableAssert;
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

    @Test
    @DisplayName("생성된 JWT토큰을 해독해 UID를 획득할 수 있다.")
    public void testVerify() throws Exception{
        //given
        String givenUid = "givenUid";
        Date createTime = new Date();

        String token = jwtTokenProvider.sign(givenUid, createTime);
        //when
        String actual = jwtTokenProvider.verify(token);
        //then
        assertThat(actual).isEqualTo(givenUid);

    }

    @Test
    @DisplayName("만료된 토큰을 해독하는 경우 ExpiredTokenException을 throw할 수 있다.")
    public void testVerifyExpiredToken() throws Exception{
        //given
        String givenUid = "givenUid";
        Date createTime = new Date(1000000000000L); //약 2001년 9월

        String expiredToken = jwtTokenProvider.sign(givenUid, createTime);
        //when

        ThrowableAssert.ThrowingCallable callable = ()->jwtTokenProvider.verify(expiredToken);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("잘못된 토큰을 해독하는 경우 JwtTokenException을 throw한다.")
    public void testVerifyInvalidToken() throws Exception{
        //given
        String invalidToken = "123";
        //when
        ThrowableAssert.ThrowingCallable callable = ()->jwtTokenProvider.verify(invalidToken);
        //then
        assertThatThrownBy(callable)
                .isInstanceOf(JwtException.class)
                .hasMessage(UserExceptonMessages.INVALID_TOKEN.getMessage());
    }


}