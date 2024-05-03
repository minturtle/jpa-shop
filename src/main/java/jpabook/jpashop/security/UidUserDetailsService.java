package jpabook.jpashop.security;

import jpabook.jpashop.exception.user.CannotFindUserException;
import org.springframework.security.core.userdetails.UserDetails;

public interface UidUserDetailsService {

    UserDetails loadUserByUid(String uid) throws CannotFindUserException;

}
