package jpabook.jpashop.controller.ui.home;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeUIController {

    @GetMapping("/")
    public String homePage(){
        return "home";
    }

}
