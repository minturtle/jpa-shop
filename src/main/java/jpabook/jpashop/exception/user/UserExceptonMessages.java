package jpabook.jpashop.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserExceptonMessages {

    INVALID_PASSWORD("비밀번호는 영문, 숫자, 특수문자를 모두 포함하며, 8자 이상이여야 한다."),
    ALREADY_EXISTS_EMAIL("이미 가입된 이메일입니다.");
    String message;

}
