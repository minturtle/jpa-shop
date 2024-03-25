package jpabook.jpashop.service;


import jpabook.jpashop.domain.Cart;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {


    private final UserRepository userRepository;

    private final ProductRepository productRepository;


    /**
     * @description 상품을 장바구니에 추가하는 메서드
     * @author minseok kim
     * @param userUid 유저의 고유식별자
     * @param dtoList 상품 고유식별자, 갯수 dto list
     * @throws
    */
    public void addCarts(String userUid, List<CartDto.Add> dtoList) throws CannotFindEntityException {
        User user = getUserOrThrow(userUid);

        for(CartDto.Add dto : dtoList){
            Product product = getProductOrThrow(dto.getProductUid());


            Cart cart = Cart.builder()
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build();
            user.addCart(cart);
        }



    }

    private Product getProductOrThrow(String productUid) throws CannotFindEntityException {
        Product product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));
        return product;
    }

    private User getUserOrThrow(String userUid) throws CannotFindEntityException {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));
    }
}
