package jpabook.jpashop.controller;

import jpabook.jpashop.controller.request.MemberRequest;
import jpabook.jpashop.controller.response.MemberResponse;
import jpabook.jpashop.service.UserService;
import lombok.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    public void signIn(@RequestBody MemberRequest.Create newMemberInfo){

    }

    @PostMapping("/login")
    public void login(@RequestBody MemberRequest.Login loginDto){

    }

    @GetMapping("/detail")
    public MemberResponse.Detail getMemberDetail(){
        return null;
    }

    @PutMapping("")
    public void update(@RequestBody MemberRequest.Update req){

    }

    @PostMapping("/logout")
    public void logout() {

    }

}


