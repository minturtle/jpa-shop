package jpabook.jpashop.controller.order;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.common.request.OrderRequest;
import jpabook.jpashop.controller.common.response.OrderResponse;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.asm.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Loggable
@Tag(name = "사용자의 주문에 관련한 API입니다.")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Operation(
            summary = "주문 수행 API",
            description = "사용자의 주문을 수행하는 API입니다. 주문 상품 중 카트에 있는 것들은 자동 제거 됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 정상 처리"),
                    @ApiResponse(responseCode = "404", description = "상품 조회 실패 시"),
                    @ApiResponse(responseCode = "400", description = "상품 재고 또는 잔고 부족 시")
            })
    @PostMapping("")
    public OrderResponse.Detail doOrder(@LoginedUserUid String userUid, @RequestBody OrderRequest.Create requestBody)
            throws CannotFindEntityException, InvalidBalanceValueException, InvalidStockQuantityException {
        OrderDto.Detail orderResult = orderService.order(
                userUid,
                requestBody.getAccountUid(),
                requestBody.getProducts()
                        .stream().map(product -> modelMapper.map(product, OrderDto.OrderProductRequestInfo.class)).toList());

        return modelMapper.map(orderResult, OrderResponse.Detail.class);
    }


    @Operation(
            summary = "사용자의 주문 정보 리스트를 조회하는 API",
            description = "사용자의 주문 정보를 조회하는 API로, 로그인된 유저의 주문 정보를 조회해 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인 정보 조회 실패 시")
            }
    )
    @GetMapping("/list")
    public PaginationListDto<OrderResponse.Preview> getOrderbyUser(
            @LoginedUserUid String userUid,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ){

        PaginationListDto<OrderDto.Preview> result = orderService.findByUser(userUid, PageRequest.of(page - 1, size));

        return new PaginationListDto<>(result.getCount(), result.getData().stream()
                .map(orderDto -> modelMapper.map(orderDto, OrderResponse.Preview.class)).toList());
    }



    @Operation(
            summary = "주문 상세 정보 조회 API",
            description = "사용자의 주문 상세 정보를 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 반환"),
                    @ApiResponse(responseCode = "404", description = "주문 정보 조회 실패"),
                    @ApiResponse(responseCode = "403", description = "현재 로그인된 사용자와 주문자가 다른 경우")

            }
    )
    // TODO : 현재 로그인된 사용자와 주문자 검증 필요
    @GetMapping("/{orderUid}")
    public OrderResponse.Detail getOrderDetail(
            @PathVariable(name="orderUid") String orderUid
    ) throws CannotFindEntityException {
        return modelMapper.map(orderService.findByOrderId(orderUid), OrderResponse.Detail.class);
    }


    @Operation(
            summary = "주문 취소 API",
            description = "주문 취소(환불)을 수행하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "404", description = "주문 정보 조회 실패"),
                    @ApiResponse(responseCode = "403", description = "현재 로그인된 사용자와 주문자가 다른 경우")
            }
    )
    // TODO : 현재 로그인된 사용자와 주문자 검증 필요
    @PostMapping("/{orderUid}/cancel")
    public void cancelOrder(
            @PathVariable(name="orderUid") String orderUid
    ) throws CannotFindEntityException, InvalidBalanceValueException, UnauthorizedAccountAccessException, InternalErrorException {
        orderService.cancel(orderUid);
    }
}
