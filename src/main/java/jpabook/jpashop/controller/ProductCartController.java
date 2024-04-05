package jpabook.jpashop.controller;


import jpabook.jpashop.controller.argumentResolvers.annotations.LoginedUserUid;
import jpabook.jpashop.controller.request.CartRequest;
import jpabook.jpashop.controller.response.CartResponse;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product/cart")
@RequiredArgsConstructor
public class ProductCartController {

    private final CartService cartService;

     @PostMapping("")
     public void addCart(@LoginedUserUid String userUid,  @RequestBody CartRequest.Add cartRequest) throws CannotFindEntityException {
         cartService.addCarts(userUid, List.of(new CartDto.Add(cartRequest.getProductUid(), cartRequest.getQuantity())));
     }



}
