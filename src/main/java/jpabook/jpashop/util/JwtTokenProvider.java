package jpabook.jpashop.util;


import io.jsonwebtoken.*;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private String secretKey;

    private Long ACCESS_TOKEN_EXPIRE_TIME;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expire-time}") Long ACCESS_TOKEN_EXPIRE_TIME
    ) {
        this.secretKey = secretKey;
        this.ACCESS_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME;
    }

    /**
     * methodName : sign
     * Author : Minseok Kim
     * description : 엑세스 토큰을 생성하는 메서드
     *
     * @param : userUid - 토큰을 생성하려는 유저의 고유 식별자
     * @param : now : token을 생성하는 시간
     * @return : 엑세스 토큰 리턴
     */
    public String sign(String userUid, Date now) {
        Claims claims = Jwts.claims().setSubject(userUid);

        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * methodName : verify
     * Author : minseok Kim
     * description : 엑세스 토큰의 값을 읽고 결과값을 반환하는 함수
     *
     * @param : String Token - 해독하려는 토큰
     * @return : 해독된 토큰의 uid
     */
    public String verify(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return claims.getBody().getSubject();

            // 토큰이 만료된 경우와, 순수 JWT 토큰 오류를 구별해 Exception을 Throw한다.
        }catch (ExpiredJwtException e){
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(UserExceptonMessages.INVALID_TOKEN.getMessage(), e);
        }
    }


}
