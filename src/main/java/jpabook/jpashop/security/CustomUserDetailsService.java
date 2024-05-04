package jpabook.jpashop.security;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.user.UsernamePasswordAuthInfo;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, UidUserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));


        UsernamePasswordAuthInfo usernamePasswordAuthInfo = user.getUsernamePasswordAuthInfo();

        return new UserDto.CustomUserDetails(
                user.getUid(),
                usernamePasswordAuthInfo.getUsername(),
                usernamePasswordAuthInfo.getPassword()
        );
    }


    @Override
    public UserDetails loadUserByUid(String uid) throws CannotFindUserException {
        User user = userRepository.findByUid(uid)
                .orElseThrow(CannotFindUserException::new);

        return new UserDto.CustomUserDetails(
                user.getUid(),
                null,
                null
        );


    }
}
