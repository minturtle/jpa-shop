package jpabook.jpashop.controller;


import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import jpabook.jpashop.service.OrderService;
import jpabook.jpashop.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public void doOrder(@RequestBody OrderItemListDto listDto, HttpSession session, HttpServletResponse res) throws IOException {

        Long memberId = SessionUtils.getUserFromSession(session);
        orderService.order(memberId , listDto);

        res.sendRedirect("/");
    }

    @GetMapping("/orders")
    public List<OrderDto.OrderPreviewDto> getOrderbyMember(HttpSession session){

        Long memberId = SessionUtils.getUserFromSession(session);
        List<OrderDto> orderDtos = orderService.findByUser(memberId);

        return orderDtos.stream().map(orderService::createOrderPreviewDto).collect(Collectors.toList());
    }

    @GetMapping("/detail")
    public OrderDto getOrderDetail(@RequestParam(name="id") Long orderId){
        return orderService.findById(orderId);
    }

    @PostMapping("/cancel")
    public void cancelOrder(@RequestParam(name="id") Long orderId, HttpServletResponse res) throws IOException {
        orderService.cancel(orderId);

        res.sendRedirect("/");
    }
}
