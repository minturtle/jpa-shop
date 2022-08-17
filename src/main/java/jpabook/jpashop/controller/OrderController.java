package jpabook.jpashop.controller;


import jpabook.jpashop.dto.OrderItemListDto;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/")
    public String doOrder(@RequestBody OrderItemListDto listDto){
        orderService.order(null, listDto);

        return "redirect:/";
    }

}
