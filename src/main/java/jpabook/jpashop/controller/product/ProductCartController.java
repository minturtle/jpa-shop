package jpabook.jpashop.controller.product;


import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.controller.common.request.CartRequest;
import jpabook.jpashop.controller.common.response.CartResponse;
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
public class ProductCartController {

    private final CartService cartService;
    private final ModelMapper modelMapper;


     @PostMapping("")
     public void addCart(@LoginedUserUid String userUid,  @RequestBody CartRequest.Add cartRequest) throws CannotFindEntityException, CartQuantityException {
         cartService.addCarts(userUid, new CartDto.Add(cartRequest.getProductUid(), cartRequest.getQuantity()));
     }
    @GetMapping("")
    public List<CartResponse.Info> getCart(@LoginedUserUid String userUid) throws CannotFindEntityException {
        return cartService.findCartByUserUid(userUid).stream().map(dto->modelMapper.map(dto, CartResponse.Info.class)).toList();
    }

    @PutMapping("")
    public void updateCart(@LoginedUserUid String userUid, @RequestBody CartRequest.Update cartRequest) throws CannotFindEntityException, CartQuantityException {
        cartService.updateCart(userUid, new CartDto.Update(cartRequest.getProductUid(), cartRequest.getAddCount()));
    }

    @DeleteMapping("{productUid}")
    public void deleteCart(@LoginedUserUid String userUid, @PathVariable("productUid") String productUid) throws CannotFindEntityException {
        cartService.deleteCart(userUid, productUid);
    }

}
