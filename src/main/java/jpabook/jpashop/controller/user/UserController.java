package jpabook.jpashop.controller.user;

import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.common.request.UserRequest;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.AuthenticateFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserAuthTypeException;
import jpabook.jpashop.service.UserService;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.*;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Loggable
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
        return null;
    }

    @GetMapping("/info")
    public UserResponse.Detail getMemberDetail(@LoginedUserUid String uid) throws CannotFindEntityException {
        UserDto.Detail userInfo = userService.getUserInfo(uid);

        return new UserResponse.Detail(
                userInfo.getUserUid(),
                userInfo.getName(),
                new AddressInfo(userInfo.getAddress(), userInfo.getDetailedAddress()),
                userInfo.getEmail(),
                userInfo.getProfileImage()
        );
    }

    @PutMapping("/info")
    public void update(@LoginedUserUid String uid, @RequestBody UserRequest.Update req) throws CannotFindEntityException {
        userService.updateUserInfo(uid, new UserDto.UpdateDefaultUserInfo(
                Optional.ofNullable(req.getName()),
                Optional.ofNullable(req.getAddressInfo().getAddress()),
                Optional.ofNullable(req.getAddressInfo().getDetailedAddress()),
                Optional.ofNullable(req.getProfileImageUrl()))
        );
    }

    @PutMapping("/password")
    public void updatePassword(@LoginedUserUid String uid, @RequestBody UserRequest.UpdatePassword req)
            throws CannotFindEntityException, PasswordValidationException, AuthenticateFailedException, UserAuthTypeException
    {
        userService.updatePassword(uid, new UserDto.UpdatePassword(req.getPassword(), req.getUpdatedPassword()));
    }


}


