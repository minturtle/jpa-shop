package jpabook.jpashop.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserExceptonMessages {

    INVALID_PASSWORD_EXPRESSION("비밀번호는 영문, 숫자, 특수문자를 모두 포함하며, 8자 이상이여야 합니다."),
    INVALID_PASSWORD("비밀번호가 잘못되었습니다."),
    ALREADY_EXISTS_EMAIL("이미 가입된 이메일입니다."),
    ALREADY_EXISTS_USERNAME("이미 존재하는 회원 ID입니다. 다른 회원 ID로 변경해 주세요"),
    LOGIN_FAILED("아이디 혹은 비밀번호가 잘못되었습니다."),
    CANNOT_FIND_USER("유저 정보를 조회할 수 없습니다."),
    NO_USERNAME_PASSWORD_AUTH_INFO("ID/비밀번호 방식의 로그인을 지원하지 않는 유저입니다."),
    UPDATE_FAILED("유저 정보 업데이트에 실패했습니다."),
    INVALID_TOKEN("토큰 값이 올바르지 않습니다.");
    String message;

}
