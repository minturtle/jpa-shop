package jpabook.jpashop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.response.ProductResponse;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private JwtTokenProvider tokenProvider;


    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 검색 조건 없이 검색할 시 DB에서 조회해 상품 정보를 반환한다.")
    void testWhenSearchProductThenReturnList() throws Exception{
        // given

        // when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        ProductResponse.Preview result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ProductResponse.Preview.class);





    }


}