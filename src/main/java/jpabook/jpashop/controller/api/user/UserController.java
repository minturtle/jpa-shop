package jpabook.jpashop.controller.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.api.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.api.common.request.UserRequest;
import jpabook.jpashop.controller.api.common.response.UserResponse;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.AuthenticateFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserAuthTypeException;
import jpabook.jpashop.service.UserService;
import lombok.*;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Loggable
@Tag(name = "사용자의 정보를 관리하는 API입니다.")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;


    @Operation(
            summary = "회원가입 API",
            description = "ID/PW 방식의 회원을 추가하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "400", description = "사용자의 username 또는 email이 이미 존재하는 경우, Password가 8자 이상, 영문 숫자 특수문자 1글자이상 포함 조건을 만족하지 못한 경우"),

            }
    )
    @PostMapping("/new")
    public void signIn(@RequestBody UserRequest.Create newMemberInfo) throws PasswordValidationException, AlreadyExistsUserException {
        UserDto.UsernamePasswordUserRegisterInfo dto = modelMapper.map(newMemberInfo, UserDto.UsernamePasswordUserRegisterInfo.class);
        userService.register(dto);
    }

    @Operation(
            summary = "ID/PW 방식 로그인 API",
            description = "ID/PW 방식의 로그인 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패시"),

            }
    )
    @PostMapping("/login")
    public UserResponse.Login login(@RequestBody UserRequest.Login loginDto) throws AuthenticateFailedException {
        return null;
    }


    @Operation(
            summary = "로그인된 사용자 정보 조회 API",
            description = "로그인된 사용자 정보 조회 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시"),

            }
    )
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


    @Operation(
            summary = "로그인된 사용자 정보 수정 API",
            description = "로그인된 사용자 수정 API입니다. 수정하지 않은 정보는 NULL로 넘겨주면 됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시"),

            }
    )
    @PutMapping("/info")
    public void update(@LoginedUserUid String uid, @RequestBody UserRequest.Update req) throws CannotFindEntityException {
        userService.updateUserInfo(uid, new UserDto.UpdateDefaultUserInfo(
                Optional.ofNullable(req.getName()),
                Optional.ofNullable(req.getAddressInfo().getAddress()),
                Optional.ofNullable(req.getAddressInfo().getDetailedAddress()),
                Optional.ofNullable(req.getProfileImageUrl()))
        );
    }


    @Operation(
            summary = "비밀번호 변경 API",
            description = "ID/PW 방식의 회원을 추가하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시, 현재 비밀번호를 틀렸을 시"),
                    @ApiResponse(responseCode = "400", description = "Password가 8자 이상, 영문 숫자 특수문자 1글자이상 포함 조건을 만족하지 못한 경우, ID/PW 방식의 인증 유저가 아닌 경우"),

            }
    )
    @PutMapping("/password")
    public void updatePassword(@LoginedUserUid String uid, @RequestBody UserRequest.UpdatePassword req)
            throws CannotFindEntityException, PasswordValidationException, AuthenticateFailedException, UserAuthTypeException
    {
        userService.updatePassword(uid, new UserDto.UpdatePassword(req.getPassword(), req.getUpdatedPassword()));
    }


}


