package jpabook.jpashop.controller.user;


import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.common.request.UserAccountRequest;
import jpabook.jpashop.controller.common.response.UserAccountResponse;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
import jpabook.jpashop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/account")
@Loggable
public class UserAccountController {

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    @PostMapping("")
    public UserAccountResponse.Create addAccount(@LoginedUserUid String userUid, @RequestBody UserAccountRequest.Create reqBody) throws CannotFindEntityException {
        String accountUid = accountService.addAccount(new AccountDto.Create(userUid, reqBody.getAccountName(), 0L));

        return new UserAccountResponse.Create(accountUid);
    }

    @GetMapping("/list")
    public List<UserAccountResponse.Info> getAccountList(@LoginedUserUid String userUid) throws CannotFindEntityException {
        return accountService.findByUser(userUid)
                .stream().map(ac->modelMapper.map(ac, UserAccountResponse.Info.class)).toList();
    }

    @PostMapping("/deposit")
    public UserAccountResponse.CashflowResult deposit(@LoginedUserUid String userUid, @RequestBody UserAccountRequest.CashFlowRequest reqBody)
            throws CannotFindEntityException, InvalidBalanceValueException, UnauthorizedAccountAccessException {
        AccountDto.CashFlowResult result = accountService.deposit(new AccountDto.CashFlowRequest(userUid, reqBody.getAccountUid(), reqBody.getAmount()));

        return modelMapper.map(result, UserAccountResponse.CashflowResult.class);
    }



}
