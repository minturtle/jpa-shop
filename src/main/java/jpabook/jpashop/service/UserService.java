package jpabook.jpashop.service;

import jpabook.jpashop.exception.user.LoginFailedException;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;


    /**
     * @description 회원가입 API
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws
    */
    public String register(UserDto.UsernamePasswordUserRegisterInfo registerInfo){
        return null;
    }


    public String login(String username, String password) throws LoginFailedException {
        return null;
    }

    /*
    * 사용자 마이페이지에 필요한 정보들을 제공
    *
    * @param userUid : User Entity의 uid값
    * @return : 유저의 이름, 주소 정보가 담긴 Dto
    * */
    @Transactional(readOnly = true)
    public UserDto.Detail getUserInfo(String userUid){
        return null;
    }

    /*
    * 사용자 이름 변경
    *
    * @param userUid : MemberEntity의 ID값
    * @param String modifiedName : 바꿀 이름
    * */
    public void update(String userUid){

    }


}

