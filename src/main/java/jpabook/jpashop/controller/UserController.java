package jpabook.jpashop.controller;

import jpabook.jpashop.controller.request.UserRequest;
import jpabook.jpashop.controller.response.UserResponse;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.AuthenticateFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.service.UserService;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.*;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/new")
    public void signIn(@RequestBody UserRequest.Create newMemberInfo) throws PasswordValidationException, AlreadyExistsUserException {
        UserDto.UsernamePasswordUserRegisterInfo dto = modelMapper.map(newMemberInfo, UserDto.UsernamePasswordUserRegisterInfo.class);
        userService.register(dto);
    }

    @PostMapping("/login")
    public UserResponse.Login login(@RequestBody UserRequest.Login loginDto) throws AuthenticateFailedException {
        String uid = userService.login(loginDto.getUserId(), loginDto.getPassword());

        return new UserResponse.Login(uid, jwtTokenProvider.sign(uid, new Date()));
    }

    @GetMapping("/info")
    public UserResponse.Detail getMemberDetail(){
        return null;
    }

    @PutMapping("")
    public void update(@RequestBody UserRequest.Update req){

    }

    @PostMapping("/logout")
    public void logout() {

    }

}


