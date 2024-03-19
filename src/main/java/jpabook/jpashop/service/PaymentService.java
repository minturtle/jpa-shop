package jpabook.jpashop.service;


import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final UserRepository userRepository;
    private final NanoIdProvider nanoIdProvider;

    public void addAccount(AccountDto.Create dto) throws CannotFindUserException {
        User user = findUserOrThrow(dto);

        user.addAccount(new Account(nanoIdProvider.createNanoId()));

    }

    private User findUserOrThrow(AccountDto.Create dto) throws CannotFindUserException {
        return userRepository.findByUid(dto.getUid())
                .orElseThrow(()->new CannotFindUserException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));
    }
}
