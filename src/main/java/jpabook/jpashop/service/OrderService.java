package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountService accountService;


    /**
     * @description 주문/결제 메서드
     * @author minseok kim
     * @param accountUid 결제하고자하는 계좌의 식별자
     * @param productDtoList 주문 상품, 갯수 정보
     * @throws CannotFindEntityException 상품 고유 식별자로 상품 조회에 실패한 경우
     * @throws InvalidStockQuantityException 상품의 수량이 주문수량보다 모자랄 경우
     * @throws InvalidBalanceValueException 총 결제금액이 Account의 잔액보다 작은 경우
    */
    public OrderDto.Detail order(String accountUid, List<OrderDto.OrderProductRequestInfo> productDtoList)
            throws CannotFindEntityException, InvalidStockQuantityException, InvalidBalanceValueException {
        int totalPrice = 0;

        for(OrderDto.OrderProductRequestInfo productOrderInfo : productDtoList){
            Product product = productRepository.findByUid(productOrderInfo.getProductUid())
                    .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

            product.removeStock(productOrderInfo.getQuantity());
            totalPrice += product.getPrice() * productOrderInfo.getQuantity();
        }



        accountService.withdraw(new AccountDto.CashFlowRequest(accountUid, totalPrice));
        return null;
    }


    /**
     * @description 주문 취소 메서드
     * @author minseok kim
     * @param orderUid 주문 식별자
     * @throws
    */
    public void cancel(String orderUid){

    }


    /**
     * @description 주문 상세 조회 메서드
     * @author minseok kim
     * @param orderUid 주문 데이터 식별자
     * @throws
    */
    public OrderDto findByOrderId(String orderUid){
        return null;
    }


    /**
     * @description 사용자의 주문 리스트 조회 API
     * @author minseok kim
     * @param
     * @throws
    */
    public List<OrderDto> findByUser(String userUid) throws EntityNotFoundException{
        return null;
    }



}
