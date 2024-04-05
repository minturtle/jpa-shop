package jpabook.jpashop.controller;


import jpabook.jpashop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product/cart")
@RequiredArgsConstructor
public class ProductCartController {

    private final CartService cartService;



}
