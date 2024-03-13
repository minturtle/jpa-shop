package jpabook.jpashop.controller;

import jpabook.jpashop.controller.request.MemberRequest;
import jpabook.jpashop.controller.response.MemberResponse;
import jpabook.jpashop.service.MemberService;
import lombok.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

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


