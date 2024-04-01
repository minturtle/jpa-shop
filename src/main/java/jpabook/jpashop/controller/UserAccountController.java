package jpabook.jpashop.controller;


import jpabook.jpashop.controller.argumentResolvers.annotations.LoginedUserUid;
import jpabook.jpashop.controller.response.UserAccountResponse;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/account")
public class UserAccountController {

    private final AccountService accountService;



    @PostMapping("")
    public UserAccountResponse.Create addAccount(@LoginedUserUid String userUid) throws CannotFindEntityException {
        String accountUid = accountService.addAccount(new AccountDto.Create(userUid, 0L));

        return new UserAccountResponse.Create(accountUid);
    }

}
