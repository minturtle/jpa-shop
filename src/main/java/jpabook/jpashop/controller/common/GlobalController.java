package jpabook.jpashop.controller.common;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GlobalController {

    @RequestMapping("/health-check")
    public String healthCheck(){
        return "hello!";
    }

}
