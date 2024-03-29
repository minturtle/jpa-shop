package jpabook.jpashop.controller;


import jpabook.jpashop.controller.request.OrderRequest;
import jpabook.jpashop.controller.response.OrderResponse;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public void doOrder(@RequestBody OrderRequest.ItemInfoList requestItems){

    }

    @GetMapping("/orders")
    public PaginationListDto<OrderResponse.Preview> getOrderbyMember(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ){
        return null;
    }

    @GetMapping("/{orderId}")
    public OrderResponse.Detail getOrderDetail(
            @PathVariable(name="orderId") String id
    ){
        return null;
    }

    @PostMapping("/{orderId}/cancel")
    public void cancelOrder(
            @PathVariable(name="orderId") String id
    ){

    }
}
