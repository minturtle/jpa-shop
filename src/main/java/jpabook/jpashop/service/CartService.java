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
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * @param dto 상품 고유식별자, 갯수 dto
     * @throws
    */
    public void addCarts(String userUid, CartDto.Add dto) throws CannotFindEntityException {
        User user = getUserOrThrow(userUid);

        Optional<Cart> isAlreadyExistsOptional = user.getCartList().stream()
                .filter(cart -> cart.getProduct().getUid().equals(dto.getProductUid()))
                .findAny();

        if(isAlreadyExistsOptional.isPresent()){
            Cart cart = isAlreadyExistsOptional.get();
            cart.addQuantity(dto.getQuantity());
            return;
        }
        Cart cart = createCart(dto);
        user.addCart(cart);




    }


    /**
     * @description 현재 유저가 담은 장바구니 리스트를 조회하는 메서드
     * @author minseok kim
     * @param userUid 유저의 고유식별자
     * @throws
    */
    public List<CartDto.Detail> findCartByUserUid(String userUid) throws CannotFindEntityException {
        User user = getUserWithProduct(userUid);

        List<CartDto.Detail> resultList = new ArrayList<>(user.getCartList().size());

        for(Cart cart : user.getCartList()){
            Product product = cart.getProduct();
            CartDto.Detail dto = new CartDto.Detail(
                    product.getUid(),
                    product.getName(),
                    product.getThumbnailImageUrl(),
                    product.getPrice(),
                    cart.getQuantity()
            );

            resultList.add(dto);
        }


        return resultList;
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

    private User getUserWithProduct(String userUid) throws CannotFindEntityException {
        return userRepository.findByUidJoinCartProduct(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));

    }
    private  Cart createCart(CartDto.Add dto) throws CannotFindEntityException {
        Product product = getProductOrThrow(dto.getProductUid());

        Cart cart = Cart.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .build();
        return cart;
    }

}
