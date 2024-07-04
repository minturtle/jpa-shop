package jpabook.jpashop.service;


import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.CartExceptionMessages;
import jpabook.jpashop.exception.product.CartQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Loggable
@Slf4j
public class CartService {


    private final UserRepository userRepository;

    private final ProductRepository productRepository;


    /**
     * 상품을 장바구니에 추가하는 메서드
     * @author minseok kim
     * @param userUid 유저의 고유식별자
     * @param dto 카트에 추가하려는 상품의 고유식별자, 갯수
     * @throws CannotFindEntityException 상품 조회 실패 시
     * @throws CartQuantityException 카트의 담으려는 양이 올바르지 않은 경우
    */
    public void addCarts(String userUid, CartDto.Add dto) throws CannotFindEntityException, CartQuantityException {
        log.info("add cart logic started : user-{}, product-{}, quantity-{}", userUid, dto.getProductUid(), dto.getQuantity());
        User user = findUserOrThrow(userUid);

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

        log.info("add cart logic finished : user-{}, product-{}, quantity-{}", userUid, dto.getProductUid(), dto.getQuantity());

    }


    /**
     * 현재 유저가 담은 장바구니 리스트를 조회하는 메서드
     * @author minseok kim
     * @param userUid 유저의 고유식별자
     * @throws CannotFindUserException 유저 정보 조회 실패시
    */
    public List<CartDto.Detail> findCartByUser(String userUid) throws CannotFindUserException {
        log.info("find cart by userUid logic started : user-{}", userUid);

        User user = getUserWithCart(userUid);

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

        log.info("find cart by userUid logic finished : user-{}", userUid);
        return resultList;
    }


    /**
     * 사용자의 카트의 갯수를 조절하는 메서드
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @param dto 카트 업데이트 정보(카트 고유 식별자, 증가하려는 갯수)
     * @throws CannotFindEntityException Product Entity를 찾을 수 없는 경우
     * @throws CartQuantityException 카트의 수량이 불가능한 경우
    */
    public void updateCart(String userUid, CartDto.Update dto) throws CannotFindEntityException, CartQuantityException {
        User user = findUserOrThrow(userUid);

        Cart cart = user.getCartList().stream()
                .filter(c -> c.getProduct().getUid().equals(dto.getProductUid()))
                .findFirst()
                .orElseThrow(() -> new CannotFindEntityException(CartExceptionMessages.CART_NOT_FOUND.getMessage()));

        cart.addQuantity(dto.getAddCount());

    }


    /**
     * 사용자의 카트 정보를 제거하는 메서드
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @param productUid 상품의 고유 식별자
     * @throws CannotFindEntityException 상품 조회 실패시
    */
    public void deleteCart(String userUid, String productUid) throws CannotFindEntityException {
        User user = userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));

        Cart cart = user.getCartList().stream()
                .filter(c -> c.getProduct().getUid().equals(productUid))
                .findFirst()
                .orElseThrow(() -> new CannotFindEntityException(CartExceptionMessages.CART_NOT_FOUND.getMessage()));

        user.removeCart(cart);
    }


    private Product findProductOrThrow(String productUid) throws CannotFindEntityException {
        Product product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));
        return product;
    }

    private User findUserOrThrow(String userUid) throws CannotFindEntityException {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));
    }

    private User getUserWithCart(String userUid) throws CannotFindUserException {
        return userRepository.findByUidJoinCartProduct(userUid)
                .orElseThrow(CannotFindUserException::new);

    }
    private Cart createCart(CartDto.Add dto) throws CannotFindEntityException {
        Product product = findProductOrThrow(dto.getProductUid());

        Cart cart = Cart.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .build();
        return cart;
    }

}
