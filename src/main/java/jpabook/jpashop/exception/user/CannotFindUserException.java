package jpabook.jpashop.exception.user;

import jpabook.jpashop.exception.common.CannotFindEntityException;

public class CannotFindUserException extends CannotFindEntityException {

    public CannotFindUserException() {
        super(UserExceptonMessages.CANNOT_FIND_USER.getMessage());
    }

}
