package jpabook.jpashop.controller.order;


import jpabook.jpashop.controller.common.request.OrderRequest;
import jpabook.jpashop.controller.common.response.OrderResponse;
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
    public void doOrder(@RequestBody OrderRequest.ItemInfoList requestBody){
    }

    @GetMapping("/list")
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
