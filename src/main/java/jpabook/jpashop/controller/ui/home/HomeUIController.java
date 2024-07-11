package jpabook.jpashop.controller.ui.home;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeUIController {

    @GetMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute("pageType", "home");
        return "home";

    }

}
