package jpabook.jpashop.controller;


import jpabook.jpashop.controller.argumentResolvers.annotations.LoginedUserUid;
import jpabook.jpashop.controller.request.UserAccountRequest;
import jpabook.jpashop.controller.response.UserAccountResponse;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/account")
public class UserAccountController {

    private final AccountService accountService;



    @PostMapping("")
    public UserAccountResponse.Create addAccount(@LoginedUserUid String userUid, @RequestBody UserAccountRequest.Create reqBody) throws CannotFindEntityException {
        String accountUid = accountService.addAccount(new AccountDto.Create(userUid, reqBody.getAccountName(), 0L));

        return new UserAccountResponse.Create(accountUid);
    }

    @GetMapping("/list")
    public UserAccountResponse.Info getAccountList(@LoginedUserUid String userUid) throws CannotFindEntityException {
        List<AccountDto.Info> accountList = accountService.findByUser(userUid);
        return null;
    }
}
