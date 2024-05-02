package jpabook.jpashop.security;

import jpabook.jpashop.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;


@Component
@RequiredArgsConstructor
public class PasswordEncodeAdapter implements PasswordEncoder {

    private final PasswordUtils passwordUtils;


    @Override
    public String encode(CharSequence rawPassword) {
        if(!rawPassword.equals("userNotFoundPassword")){
            throw new AuthenticationException("Method Call Error"){};
        }


        return passwordUtils.encodePassword(rawPassword.toString(), passwordUtils.createSalt());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] split = encodedPassword.split(":");
        String savedEncodedPassword = split[0];
        byte[] salt = Base64.getDecoder().decode(split[1]);

        return passwordUtils.matches(rawPassword.toString(), salt, savedEncodedPassword);
    }
}
