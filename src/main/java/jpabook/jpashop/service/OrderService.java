package jpabook.jpashop.service;

import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.Payment;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.util.NanoIdProvider;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final NanoIdProvider nanoIdProvider;

    /**
     * @description 주문/결제 메서드
     * @author minseok kim
     * @param userUid 결제하고자 하는 사용자의 식별자
     * @param accountUid 결제하고자하는 계좌의 식별자
     * @param productDtoList 주문 상품, 갯수 정보
     * @throws CannotFindEntityException 상품,유저,계좌 고유 식별자로 조회에 실패한 경우(Message로 구분)
     * @throws InvalidStockQuantityException 상품의 수량이 주문수량보다 모자랄 경우
     * @throws InvalidBalanceValueException 총 결제금액이 Account의 잔액보다 작은 경우
    */
    public OrderDto.Detail order(String userUid, String accountUid, List<OrderDto.OrderProductRequestInfo> productDtoList)
            throws CannotFindEntityException, InvalidStockQuantityException, InvalidBalanceValueException {

        int totalPrice = decreaseProductStock(productDtoList);

        AccountDto.CashFlowResult cashflowResult = accountService.withdraw(new AccountDto.CashFlowRequest(accountUid, totalPrice));

        Order order = createOrderEntity(cashflowResult, userUid, productDtoList);
        orderRepository.save(order);


        return createOrderResult(productDtoList, order);


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


    private int decreaseProductStock(List<OrderDto.OrderProductRequestInfo> productDtoList) throws CannotFindEntityException, InvalidStockQuantityException {
        int totalPrice = 0;

        for(OrderDto.OrderProductRequestInfo productOrderInfo : productDtoList){
            Product product = productRepository.findByUid(productOrderInfo.getProductUid())
                    .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

            product.removeStock(productOrderInfo.getQuantity());
            totalPrice += product.getPrice() * productOrderInfo.getQuantity();
        }
        return totalPrice;
    }

    private Order createOrderEntity(AccountDto.CashFlowResult cashflowResult, String userUid, List<OrderDto.OrderProductRequestInfo> productDtoList) throws CannotFindEntityException {
        User orderUser = userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));


        Account account = accountRepository.findByUid(cashflowResult.getAccountUid())
                .orElseThrow(() -> new CannotFindEntityException(AccountExceptionMessages.CANNOT_FIND_ACCOUNT.getMessage()));

        Order order = Order.builder()
                .uid(nanoIdProvider.createNanoId())
                .user(orderUser)
                .deliveryInfo(new AddressInfo(orderUser.getAddressInfo()))
                .payment(new Payment(account, cashflowResult.getAmount()))
                .build();

        for(OrderDto.OrderProductRequestInfo productOrderInfo : productDtoList){
            Product product = productRepository.findByUid(productOrderInfo.getProductUid())
                    .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

            order.addOrderProduct(new OrderProduct(product, productOrderInfo.getQuantity(), product.getPrice()));
        }

        return order;
    }

    private OrderDto.Detail createOrderResult(List<OrderDto.OrderProductRequestInfo> productDtoList, Order order) throws CannotFindEntityException {
        OrderDto.Detail result = OrderDto.Detail.builder()
                .orderUid(order.getUid())
                .orderTime(order.getCreatedAt())
                .orderStatus(order.getStatus())
                .orderPaymentDetail(new OrderDto.OrderPaymentDetail(order.getPayment().getAccount().getUid(), order.getPayment().getAmount()))
                .build();

        for(OrderDto.OrderProductRequestInfo productOrderInfo : productDtoList){
            Product product = productRepository.findByUid(productOrderInfo.getProductUid())
                    .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));
            OrderDto.OrderedProductDetail detail = new OrderDto.OrderedProductDetail(
                    product.getUid(),
                    product.getName(),
                    product.getThumbnailImageUrl(),
                    product.getPrice(),
                    productOrderInfo.getQuantity(),
                    product.getPrice() * productOrderInfo.getQuantity()
            );
            result.addOrderProduct(detail);
        }
        return result;
    }


}
