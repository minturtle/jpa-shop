package jpabook.jpashop.service;


import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.CannotFindAccountException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final NanoIdProvider nanoIdProvider;


    /**
     * @param dto Account 추가에 필요한 정보가 있는 dto
     * @return 생성된 Account의 고유 식별자
     * @throws CannotFindUserException 유저 정보를 조회할 수 없을때
     * @author minseok kim
     * @description User의 Account를 추가하는 메서드
     */
    public String addAccount(AccountDto.Create dto) throws CannotFindUserException {
        User user = findUserOrThrow(dto);

        String accountUid = nanoIdProvider.createNanoId();
        user.addAccount(new Account(accountUid, dto.getBalance()));


        return accountUid;
    }

    /**
     * @author minseok kim
     * @description Account에 츨금하는 메서드
     * @param dto 출금에 필요한 정보가 담긴 dto
     * @exception CannotFindAccountException Account의 고유식별자로 account를 조회할 수 없을 때
     * @exception InvalidBalanceValueException 출금금액이 잔고보다 클 때
    */
    @Transactional(rollbackFor = {CannotFindUserException.class, InvalidBalanceValueException.class})
    public void withdraw(AccountDto.WithdrawDeposit dto) throws CannotFindAccountException, InvalidBalanceValueException {
        Account account = findAccountWithPessimisticLockOrThrow(dto.getAccountUid());
        account.withdraw(dto.getAmount());
    }



    @Transactional(rollbackFor = {CannotFindUserException.class, InvalidBalanceValueException.class})
    public void deposit(AccountDto.WithdrawDeposit dto) throws CannotFindAccountException, InvalidBalanceValueException {
        Account account = findAccountWithPessimisticLockOrThrow(dto.getAccountUid());
        account.deposit(dto.getAmount());
    }



    private Account findAccountOrThrow(String accountUid) throws CannotFindAccountException {
        return accountRepository.findByUid(accountUid)
                .orElseThrow(() -> new CannotFindAccountException(AccountExceptionMessages.CANNOT_FIND_ACCOUNT.getMessage()));
    }

    private Account findAccountWithPessimisticLockOrThrow(String accountUid)  throws CannotFindAccountException{
        return accountRepository.findByUidWithLock(accountUid)
                .orElseThrow(() -> new CannotFindAccountException(AccountExceptionMessages.CANNOT_FIND_ACCOUNT.getMessage()));
    }


    private User findUserOrThrow(AccountDto.Create dto) throws CannotFindUserException {
        return userRepository.findByUid(dto.getUid())
                .orElseThrow(()->new CannotFindUserException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));
    }
}
