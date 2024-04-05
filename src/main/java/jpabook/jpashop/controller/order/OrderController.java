package jpabook.jpashop.controller.order;


import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.common.request.OrderRequest;
import jpabook.jpashop.controller.common.response.OrderResponse;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;
    @PostMapping("")
    public OrderResponse.Detail doOrder(@LoginedUserUid String userUid, @RequestBody OrderRequest.Create requestBody) throws CannotFindEntityException, InvalidBalanceValueException, InvalidStockQuantityException {
        OrderDto.Detail orderResult = orderService.order(
                userUid,
                requestBody.getAccountUid(),
                requestBody.getProducts()
                        .stream().map(product -> modelMapper.map(product, OrderDto.OrderProductRequestInfo.class)).toList());

        return modelMapper.map(orderResult, OrderResponse.Detail.class);
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
