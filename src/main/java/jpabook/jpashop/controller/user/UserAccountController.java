package jpabook.jpashop.controller.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자의 계좌를 관리하는 API입니다.")
public class UserAccountController {

    private final AccountService accountService;

    private final ModelMapper modelMapper;


    @Operation(
            summary = "사용자 계좌 추가 API",
            description = "사용자의 계좌를 추가하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "사용자 정보 조회 실패시")
            }
    )
    @PostMapping("")
    public UserAccountResponse.Create addAccount(@LoginedUserUid String userUid, @RequestBody UserAccountRequest.Create reqBody) throws CannotFindEntityException {
        String accountUid = accountService.addAccount(new AccountDto.Create(userUid, reqBody.getAccountName(), 0L));

        return new UserAccountResponse.Create(accountUid);
    }


    @Operation(
            summary = "사용자 계좌 리스트 조회 API",
            description = "사용자의 계좌 리스트를 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "사용자 정보 조회 실패시")
            }
    )
    @GetMapping("/list")
    public List<UserAccountResponse.Info> getAccountList(@LoginedUserUid String userUid) throws CannotFindEntityException {
        return accountService.findAccountsByUser(userUid)
                .stream().map(ac->modelMapper.map(ac, UserAccountResponse.Info.class)).toList();
    }

    @Operation(
            summary = "사용자 계좌 입금 API",
            description = "사용자의 계좌에 돈을 입금하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "400", description = "계좌에 들어갈 금액이 올바르지 않은 경우"),
                    @ApiResponse(responseCode = "401", description = "사용자 정보 조회 실패시"),
                    @ApiResponse(responseCode = "403", description = "계좌에 접근 권한이 없을 시")
            }
    )
    @PostMapping("/deposit")
    public UserAccountResponse.CashflowResult deposit(@LoginedUserUid String userUid, @RequestBody UserAccountRequest.CashFlowRequest reqBody)
            throws CannotFindEntityException, InvalidBalanceValueException, UnauthorizedAccountAccessException {
        AccountDto.CashFlowResult result = accountService.deposit(new AccountDto.CashFlowRequest(userUid, reqBody.getAccountUid(), reqBody.getAmount()));

        return modelMapper.map(result, UserAccountResponse.CashflowResult.class);
    }



}
