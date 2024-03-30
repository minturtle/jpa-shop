package jpabook.jpashop.controller;

import jpabook.jpashop.controller.request.UserRequest;
import jpabook.jpashop.controller.response.MemberResponse;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.service.UserService;
import lombok.*;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;


    @PostMapping("/new")
    public void signIn(@RequestBody UserRequest.Create newMemberInfo) throws PasswordValidationException, AlreadyExistsUserException {
        UserDto.UsernamePasswordUserRegisterInfo dto = modelMapper.map(newMemberInfo, UserDto.UsernamePasswordUserRegisterInfo.class);
        userService.register(dto);
    }

    @PostMapping("/login")
    public void login(@RequestBody UserRequest.Login loginDto){

    }

    @GetMapping("/detail")
    public MemberResponse.Detail getMemberDetail(){
        return null;
    }

    @PutMapping("")
    public void update(@RequestBody UserRequest.Update req){

    }

    @PostMapping("/logout")
    public void logout() {

    }

}


