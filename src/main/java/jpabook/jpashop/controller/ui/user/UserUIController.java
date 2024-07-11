package jpabook.jpashop.controller.ui.user;


import jpabook.jpashop.controller.api.common.annotations.LoginedUserUid;
import jpabook.jpashop.exception.user.CannotFindUserException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserUIController {


    @GetMapping("/info")
    public String userInfo(@LoginedUserUid String uid){

        return "/user/info";
    }

    @GetMapping("/login")
    public String userLogin(){
        return "/user/login";
    }


    @ExceptionHandler(CannotFindUserException.class)
    public String noLoginUser(){
        return "redirect:/user/login";
    }

}
