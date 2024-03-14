package jpabook.jpashop.service;

import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    /**
     * @description 주문/결제 메서드
     * @author minseok kim
     * @param userUid 주문자의 식별자
     * @param orderItems 주문 상품, 갯수 정보
     * @throws
    */
    public OrderDto order(String userUid, List<OrderDto.OrderItemDetail> orderItems){
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
