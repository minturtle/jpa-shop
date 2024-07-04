package jpabook.jpashop.service;


import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@Loggable
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final NanoIdProvider nanoIdProvider;


    /**
     * User의 Account를 추가하는 메서드
     * @param dto 사용자 고유식별자, 계좌 이름, 초기 계좌 금액이 담긴 dto
     * @return 생성된 Account의 고유 식별자
     * @throws CannotFindUserException 유저 정보를 조회할 수 없을 시
     * @author minseok kim
     */
    public String addAccount(AccountDto.Create dto) throws CannotFindUserException {
        log.info("add Account Logic started : user-{}, balance-{}won", dto.getUserUid(), dto.getBalance());
        User user = findUserOrThrow(dto);

        String accountUid = nanoIdProvider.createNanoId();
        user.addAccount(new Account(accountUid, dto.getName(), dto.getBalance()));

        log.info("add Account Logic finished : user- {}, account-uid- {},balance-{}won", dto.getUserUid(), accountUid, dto.getBalance());
        return accountUid;
    }

    /**
     *
     * Account에 츨금하는 메서드
     * @param dto 출금하는 사람의 고유 식별자, 계좌 고유 식별자, 출금액이 담긴 dto
     * @exception CannotFindEntityException Account의 고유식별자로 account를 조회할 수 없을 때
     * @exception InvalidBalanceValueException 출금금액이 잔고보다 클 때
     * @exception OptimisticLockingFailureException 동시에 입/출금요청이 들어와 업데이트 된 경우
     * @author minseok kim
    */
    @Transactional(rollbackFor = {CannotFindEntityException.class, InvalidBalanceValueException.class})
    public AccountDto.CashFlowResult withdraw(AccountDto.CashFlowRequest dto) throws CannotFindEntityException, InvalidBalanceValueException, OptimisticLockingFailureException {
        Account account = findAccountWithOptimisticLockOrThrow(dto.getAccountUid());
        log.info("withdraw Logic started : account-uid : {}, before_account_balance-{}, amount-{}won", dto.getAccountUid(), account.getBalance(), dto.getAmount());

        account.withdraw(dto.getAmount());


        log.info("withdraw Logic finished : account-uid : {}, after_account_balance-{}, amount-{}won", dto.getAccountUid(), account.getBalance(), dto.getAmount());
        return new AccountDto.CashFlowResult(
                dto.getAccountUid(),
                dto.getAmount(),
                LocalDateTime.now(),
                CashFlowType.WITHDRAW,
                CashFlowStatus.DONE
        );
    }


    /**
     * Account에 입금하는 메서드
     * @author minseok kim
     * @param dto 입금에 필요한 정보가 담긴 dto
     * @exception CannotFindEntityException Account의 고유식별자로 account를 조회할 수 없을 때
     * @exception InvalidBalanceValueException 입금 후 금액이 시스템에서 정의한 계좌 최고액보다 클 때
     */
    @Transactional(rollbackFor = {CannotFindEntityException.class, InvalidBalanceValueException.class, UnauthorizedAccountAccessException.class})
    public AccountDto.CashFlowResult deposit(AccountDto.CashFlowRequest dto) throws CannotFindEntityException, InvalidBalanceValueException, UnauthorizedAccountAccessException {
        Account account = findAccountWithPessimisticLockOrThrow(dto.getAccountUid());
        log.info("deposit Logic started : account-uid : {}, before_account_balance-{}, amount-{}won", dto.getAccountUid(), account.getBalance(), dto.getAmount());


        if(!account.getUser().getUid().equals(dto.getUserUid())){
            throw new UnauthorizedAccountAccessException(AccountExceptionMessages.UNAUTHORIZED_ACCESS.getMessage());
        }

        account.deposit(dto.getAmount());

        log.info("deposit Logic finished : account-uid : {}, after_account_balance-{}, amount-{}won", dto.getAccountUid(), account.getBalance(), dto.getAmount());
        return new AccountDto.CashFlowResult(
                dto.getAccountUid(),
                dto.getAmount(),
                LocalDateTime.now(),
                CashFlowType.DEPOSIT,
                CashFlowStatus.DONE
        );

    }
    public List<AccountDto.Info> findAccountsByUser(String userUid) throws CannotFindEntityException {
        User user = userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));
        return user.getAccountList().stream().map(a -> new AccountDto.Info(a.getUid(), a.getName(), a.getBalance())).toList();

    }


    private Account findAccountWithPessimisticLockOrThrow(String accountUid) throws CannotFindEntityException{
        return accountRepository.findByUidWithPessimisticLock(accountUid)
                .orElseThrow(() -> new CannotFindEntityException(AccountExceptionMessages.CANNOT_FIND_ACCOUNT.getMessage()));
    }

    private Account findAccountWithOptimisticLockOrThrow(String accountUid) throws CannotFindEntityException{
        return accountRepository.findByUidWithOptimisticLock(accountUid)
                .orElseThrow(() -> new CannotFindEntityException(AccountExceptionMessages.CANNOT_FIND_ACCOUNT.getMessage()));

    }


    private User findUserOrThrow(AccountDto.Create dto) throws CannotFindUserException {
        return userRepository.findByUid(dto.getUserUid())
                .orElseThrow(()->new CannotFindUserException());
    }

}
