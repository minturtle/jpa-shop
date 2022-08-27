package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import jpabook.jpashop.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    private OrderDto orderDto1;
    private OrderDto orderDto2;
    private OrderDto orderDto3;

    @BeforeEach
    void setUp() {
        member = Member.createMember("김민석", "root11", "1111", "경북 구미시", "대학로 61","금오공과 대학교");
        Member member2 = Member.createMember("aa","bb","cccc", "dd", "e", "f");

        orderDto1 = new OrderDto(10L, member, LocalDateTime.now(), OrderStatus.ORDER, null, null);
        orderDto2 = new OrderDto(11L, member, LocalDateTime.now(), OrderStatus.ORDER, null, null);
        orderDto3 = new OrderDto(12L, member2, LocalDateTime.now(), OrderStatus.ORDER, null, null);

    }

    @Test
    @DisplayName("주문하기")
    void t1() throws Exception {
        //given
        MockHttpSession session = getUserSession();

        String json = createJSON(List.of(
                new OrderItemListDto.OrderItemDto(1L, "물건1", 15000, 2),
                new OrderItemListDto.OrderItemDto(2L,"물건2", 30000, 3),
                new OrderItemListDto.OrderItemDto(3L,"물건3", 12000, 5)));

        //when
        ResultActions result = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(json));

        //then
        result.andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("유저의 주문 리스트 조회하기-통합 테스트에서 확인")
    @Disabled
    void t2() throws Exception {
        MockHttpSession session = getUserSession();
        //given
        given(orderService.findByUser(1L)).willReturn(List.of(orderDto1, orderDto2));
        //when
        final String resContentString = mockMvc.perform(get("/order/orders")
                .characterEncoding(StandardCharsets.UTF_8)
                .session(session)).andReturn().getResponse().getContentAsString();

        List<OrderDto.OrderPreviewDto> resultList = List.of(objectMapper.readValue(resContentString, OrderDto.OrderPreviewDto[].class));

        //then
        assertThat(resultList.size()).isEqualTo(2);
        assertThat(resultList.get(0).getOrderId()).isEqualTo(10L);
        assertThat(resultList.get(1).getOrderId()).isEqualTo(11L);
    }

    @Test
    @DisplayName("주문 상세 조회하기")
    void t3() throws Exception {
        //given
        given(orderService.findById(10L)).willReturn(orderDto1);
        //when
        final OrderDto dto = objectMapper.readValue(mockMvc.perform(get("/order/detail").param("id", "10"))
                .andReturn().getResponse().getContentAsString(), OrderDto.class);

        //then
        assertThat(dto.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("주문 취소하기")
    void t4() throws Exception {
        //given

        //when
        final ResultActions result = mockMvc.perform(post("/order/cancel").param("id", "1"));
        //then
        result.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));

    }
    private String createJSON(List<OrderItemListDto.OrderItemDto> list) throws JsonProcessingException {
        OrderItemListDto dto = new OrderItemListDto();
        dto.setItems(list);

        String json = objectMapper.writeValueAsString(dto);
        return json;
    }
    private MockHttpSession getUserSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);
        return session;
    }
}