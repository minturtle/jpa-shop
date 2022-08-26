package jpabook.jpashop.controller;


import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import jpabook.jpashop.service.OrderService;
import jpabook.jpashop.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("")
    public String doOrder(@RequestBody OrderItemListDto listDto, HttpSession session){

        Long memberId = SessionUtils.getUserFromSession(session);
        orderService.order(memberId , listDto);

        return "redirect:/";
    }

    @GetMapping("/orders")
    public List<OrderDto> getOrderbyMember(HttpSession session){
        final Long memberId = SessionUtils.getUserFromSession(session);
        return orderService.findByUser(memberId);
    }

}
