package jpabook.jpashop.service;

import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.Payment;
import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import jpabook.jpashop.exception.order.OrderExceptionMessage;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Loggable
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
    @Transactional(rollbackFor = {CannotFindEntityException.class, InvalidStockQuantityException.class, InvalidBalanceValueException.class, RuntimeException.class})
    public OrderDto.Detail order(String userUid, String accountUid, List<OrderDto.OrderProductRequestInfo> productDtoList)
            throws CannotFindEntityException, InvalidStockQuantityException, InvalidBalanceValueException {

        int totalPrice = decreaseProductStock(productDtoList);

        AccountDto.CashFlowResult cashflowResult = accountService.withdraw(new AccountDto.CashFlowRequest(userUid, accountUid, totalPrice));

        Order order = createOrderEntity(cashflowResult, userUid, productDtoList);
        orderRepository.save(order);

        removeCartIfExists(userUid, productDtoList);

        return createOrderResult(order);


    }


    /**
     * @description 주문 취소 메서드
     * @author minseok kim
     * @param orderUid 주문 식별자
     * @throws CannotFindEntityException Order 정보를 UID로 조회하지 못한 경우
     * @throws InvalidBalanceValueException 계좌의 Balance가 최댓값을 넘은 경우
     * @throws InternalErrorException Order과 Product 또는 Account가 잘 설정되어 있지 않은 경우
    */
    @Transactional(rollbackFor = {CannotFindEntityException.class, InvalidBalanceValueException.class, InternalErrorException.class,UnauthorizedAccountAccessException.class})
    public void cancel(String orderUid) throws CannotFindEntityException, InvalidBalanceValueException, InternalErrorException, UnauthorizedAccountAccessException {
        Order order = orderRepository.findByUidWithJoinProductAccount(orderUid)
                .orElseThrow(() -> new CannotFindEntityException(OrderExceptionMessage.CANNOT_FIND_ORDER.getMessage()));

        increaseProductStock(order.getOrderProducts());
        refundPayment(order.getUser().getUid(), order.getPayment());
        order.setStatus(OrderStatus.CANCELED);

    }

    /**
     * @description 주문 상세 조회 메서드
     * @author minseok kim
     * @param orderUid 주문 데이터 식별자
     * @throws CannotFindEntityException UID 조회 실패시
    */
    public OrderDto.Detail findByOrderId(String orderUid) throws CannotFindEntityException {
        Order findOrder = orderRepository.findByUid(orderUid)
                .orElseThrow(() -> new CannotFindEntityException(OrderExceptionMessage.CANNOT_FIND_ORDER.getMessage()));

        return createOrderResult(findOrder);
    }


    /**
     * @description 사용자의 주문 리스트 조회 API
     * @author minseok kim
     * @param
     * @throws
    */
    public PaginationListDto<OrderDto.Preview> findByUser(String userUid, Pageable pageable) throws EntityNotFoundException{

        Integer count = orderRepository.countByUser(userUid);
        List<Order> orderList = orderRepository.findByUser(userUid, pageable);

        return new PaginationListDto<>(count, orderList.stream().map(this::createOrderPreview).toList()) ;
    }




    /**
     * @description 결제 취소 메서드
     * @author minseok kim
     * @param payment 결제 정보
     * @throws InvalidBalanceValueException 계좌의 잔고가 최댓값을 초과한 경우
     * @throws InternalErrorException Account 조회에 실패한 경우
     *
    */
    private void refundPayment(String userUid, Payment payment) throws InvalidBalanceValueException, InternalErrorException, UnauthorizedAccountAccessException {
        try {
            String accountUid = payment.getAccount().getUid();
            Integer amount = payment.getAmount();

            accountService.deposit(new AccountDto.CashFlowRequest(userUid, accountUid, amount));

        }catch (CannotFindEntityException e){
            throw new InternalErrorException(AccountExceptionMessages.ENTITY_ACCOUNT_MAPPING_FAILED.getMessage());
        }
    }



    /**
     * @description 주문 취소로 인해 상품의 갯수를 늘리는 메서드
     * @author minseok kim
     * @param orderProducts 주문 상품 리스트
     * @throws InternalErrorException 상품 정보 조회 실패시
    */
    private void increaseProductStock(List<OrderProduct> orderProducts) throws InternalErrorException {
        try{
            for(OrderProduct orderProduct : orderProducts){
                Product product = productRepository.findByUidWithPessimisticLock(orderProduct.getProduct().getUid())
                        .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

                product.addStock(orderProduct.getCount());
            }
        }catch (CannotFindEntityException e){
            throw new InternalErrorException(ProductExceptionMessages.ENTITY_PRODUCT_MAPPING_FAILED.getMessage());
        }
    }


    /**
     * @description 주문으로 인해 상품의 갯수를 줄이는 메서드
     * @author minseok kim
     * @param productDtoList 주문 상품 리스트
     * @throws CannotFindEntityException 상품 조회 실패시
    */
    private int decreaseProductStock(List<OrderDto.OrderProductRequestInfo> productDtoList) throws CannotFindEntityException, InvalidStockQuantityException {
        int totalPrice = 0;

        for(OrderDto.OrderProductRequestInfo productOrderInfo : productDtoList){
            Product product = productRepository.findByUidWithPessimisticLock(productOrderInfo.getProductUid())
                    .orElseThrow(() -> new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

            product.removeStock(productOrderInfo.getQuantity());
            totalPrice += product.getPrice() * productOrderInfo.getQuantity();
        }
        return totalPrice;
    }

    /**
     * @description 주문 정보 저장을 위한 주문 정보 엔티티 생성 메서드
     * @author minseok kim
     * @param cashflowResult 결제 완료 정보
     * @param userUid 주문자
     * @param productDtoList 주문 상품 리스트
     * @throws CannotFindEntityException Account / User 고유 식별자 조회 실패시
    */
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

            order.addOrderProduct(new OrderProduct(product, productOrderInfo.getQuantity()));
        }

        return order;
    }

    /**
     * @param
     * @throws
     * @description 주문 결과정보를 생성하는 메서드
     * @author minseok kim
     */
    private OrderDto.Detail createOrderResult(Order order) {
        OrderDto.Detail result = OrderDto.Detail.builder()
                .orderUid(order.getUid())
                .orderTime(order.getCreatedAt())
                .orderStatus(order.getStatus())
                .orderPaymentDetail(new OrderDto.OrderPaymentDetail(order.getPayment().getAccount().getUid(), order.getPayment().getAmount()))
                .build();

        for(OrderProduct orderProduct : order.getOrderProducts()){
            Product product = orderProduct.getProduct();

            OrderDto.OrderedProductDetail detail = new OrderDto.OrderedProductDetail(
                    product.getUid(),
                    product.getName(),
                    product.getThumbnailImageUrl(),
                    orderProduct.getItemPrice(),
                    orderProduct.getCount(),
                    orderProduct.getItemPrice() * orderProduct.getCount()
            );
            result.addOrderProduct(detail);
        }
        return result;
    }

        private  OrderDto.Preview createOrderPreview(Order o) {
            String firstProductName = o.getOrderProducts().get(0).getProduct().getName();
            int productCount = o.getOrderProducts().size() - 1;
            String name = firstProductName + "외 " + productCount + "건";

            return new OrderDto.Preview(
                    o.getUid(),
                    name,
                    o.getPayment().getAmount(),
                    o.getCreatedAt(),
                    o.getStatus()
            );
        }


    private void removeCartIfExists(String userUid, List<OrderDto.OrderProductRequestInfo> productDtoList) throws CannotFindEntityException {
        User user = userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()));


        List<Cart> cartsToRemove = new ArrayList<>(user.getCartList().size());

        for (Cart cart : user.getCartList()) {
            if (productDtoList.stream().anyMatch(product -> product.getProductUid().equals(cart.getProduct().getUid()))) {
                cartsToRemove.add(cart);
            }
        }

        for (Cart cartToRemove : cartsToRemove) {
            user.removeCart(cartToRemove);
        }
    }
}
