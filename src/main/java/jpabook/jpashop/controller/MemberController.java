package jpabook.jpashop.controller;


import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.dto.MemberDto;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.util.SessionUtils;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/signIn")
    public ResponseEntity signIn(@RequestBody MemberDto memberDto)throws MemberService.RegisterFailed {
        memberService.register(memberDto);

        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MemberDto memberDto, HttpSession session) throws MemberService.LoginFailed {
        Long memberEntityId = memberService.login(memberDto);

        SessionUtils.putUserToSession(session, memberEntityId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<MemberDetailResponse> getMemberDetail(HttpSession session){
        Long memberEntityId = SessionUtils.getUserFromSession(session);

        MemberDto memberDetail = memberService.getMemberDetail(memberEntityId);

        return new ResponseEntity<>(new MemberDetailResponse(memberDetail.getUsername(), memberDetail.getAddress().toString()),
                        HttpStatus.OK);
    }

    @PatchMapping("/name")
    public ResponseEntity updateMemberName(@RequestBody MemberUpdateRequest req, HttpSession session){
        Long memberEntityId = SessionUtils.getUserFromSession(session);

        memberService.updateName(memberEntityId, req.getName());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/address")
    public ResponseEntity updateMemberAddress(@RequestBody Address address, HttpSession session){
        Long memberEntityId = SessionUtils.getUserFromSession(session);

        memberService.updateAddress(memberEntityId, address);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/password")
    public ResponseEntity updateMemberPassword(@RequestBody MemberUpdateRequest req, HttpSession session){
        Long memberEntityId = SessionUtils.getUserFromSession(session);

        memberService.updatePassword(memberEntityId, req.getPassword());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpSession session){
        SessionUtils.logout(session);
        return new ResponseEntity(HttpStatus.OK);
    }


    @ExceptionHandler({MemberService.RegisterFailed.class, MemberService.LoginFailed.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }


}

// ==== request ====

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
class MemberUpdateRequest{
    private String name;
    private String password;
}


// ==== response ====

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
class MemberDetailResponse{
    private String name;
    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDetailResponse that = (MemberDetailResponse) o;
        return Objects.equals(name, that.name) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}