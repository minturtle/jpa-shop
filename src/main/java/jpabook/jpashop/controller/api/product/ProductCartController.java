package jpabook.jpashop.controller.api.product;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.api.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.api.common.request.CartRequest;
import jpabook.jpashop.controller.api.common.response.CartResponse;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.CartQuantityException;
import jpabook.jpashop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/cart")
@RequiredArgsConstructor
@Loggable
@Tag(name="사용자의 카트 정보를 관리하는 API입니다.")
public class ProductCartController {

    private final CartService cartService;
    private final ModelMapper modelMapper;


    @Operation(
            summary = "카트 추가 API",
            description = "사용자가 특정 상품을 특정 갯수만큼 카트에 담는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "404", description = "상품 정보 조회 실패 시"),
                    @ApiResponse(responseCode = "400", description = "상품의 남은 갯수가 사용자가 담으려는 갯수보다 적을 시")
            }
    )
     @PostMapping("")
     public void addCart(@LoginedUserUid String userUid,  @RequestBody CartRequest.Add cartRequest) throws CannotFindEntityException, CartQuantityException {
         cartService.addCarts(userUid, new CartDto.Add(cartRequest.getProductUid(), cartRequest.getQuantity()));
     }

     @Operation(
             summary = "카트 정보 조회 API",
             description = "현재 로그인된 사용자의 카트 정보를 조회하는 API입니다.",
             responses = {
                     @ApiResponse(responseCode = "200", description = "정상 처리"),
                     @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시")
             }
     )
    @GetMapping("")
    public List<CartResponse.Info> getCart(@LoginedUserUid String userUid) throws CannotFindEntityException {
        return cartService.findCartByUser(userUid).stream().map(dto->modelMapper.map(dto, CartResponse.Info.class)).toList();
    }


    @Operation(
            summary = "카트 정보 수정 API",
            description = "사용자의 카트에 담긴 상품의 갯수를 수정하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "400", description = "상품의 남은 갯수가 사용자가 수정하는 갯수보다 적을 시"),
                    @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시"),
                    @ApiResponse(responseCode = "404", description = "카트 정보 또는 상품 정보가 조회되지 않을 시")
            }
    )
    @PutMapping("")
    public void updateCart(@LoginedUserUid String userUid, @RequestBody CartRequest.Update cartRequest) throws CannotFindEntityException, CartQuantityException {
        cartService.updateCart(userUid, new CartDto.Update(cartRequest.getProductUid(), cartRequest.getAddCount()));
    }

    @Operation(
            summary = "카트 정보 삭제 API",
            description = "사용자의 카트에 담긴 상품을 삭제하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "401", description = "로그인된 사용자 정보 조회 실패시"),
                    @ApiResponse(responseCode = "404", description = "카트 정보 또는 상품 정보가 조회되지 않을 시")
            }
    )
    @DeleteMapping("{productUid}")
    public void deleteCart(@LoginedUserUid String userUid, @PathVariable("productUid") String productUid) throws CannotFindEntityException {
        cartService.deleteCart(userUid, productUid);
    }

}
