package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.ItemDto;
import jpabook.jpashop.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import javax.persistence.EntityNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private  ItemDto itemDto1;
    private  ItemDto itemDto2;
    @BeforeEach
    void setUp() {
            itemDto1 = new ItemDto.ItemDtoBuilder().putItemId(1L).setItemType(Item.class)
                    .putItemField("물건1", 15000 , 30).setDescription("hello").build();

            itemDto2 = new ItemDto.ItemDtoBuilder().putItemId(2L).setItemType(Item.class)
                    .putItemField("물건2", 30000, 20).build();
    }

    @Test
    @DisplayName("아이템 리스트 조회하기")
    void t1() throws Exception {
        //given
        given(itemService.findAll(1, ItemService.SortType.최신순)).willReturn(List.of(itemDto1, itemDto2));

        //when
        final ItemListResponse actualResult = getResponseBody(
                mockMvc.perform(get("/item/items").param("page", "1")),
                ItemListResponse.class);

        assertThat(actualResult.getItems().get(0).getItemId()).isEqualTo(1);
        assertThat(actualResult.getItems().get(1).getItemId()).isEqualTo(2);
        assertThat(actualResult.getItems().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("아이템 상세 조회하기")
    void t2() throws Exception {
        //given
        given(itemService.findById(1L)).willReturn(itemDto1);

        //when
        final ItemDetailResponse actualResponse = getResponseBody(mockMvc.perform(get("/item/detail").param("itemId", "1")),
                ItemDetailResponse.class);
        //then
        assertThat(actualResponse.getItemId()).isEqualTo(1L);
        assertThat(actualResponse.getPrice()).isEqualTo(15000);
        assertThat(actualResponse.getDescription()).isEqualTo("hello");
    }

    @Test
    @DisplayName("이름으로 검색하기")
    void t3() throws Exception {
        //given
        given(itemService.findByName("물건1")).willReturn(itemDto1);

        //when
        ItemListResponse.ItemPreview actualResponse = getResponseBody(mockMvc.perform(get("/item/search").param("itemName", "물건1")), ItemListResponse.ItemPreview.class);
        //then
        assertThat(actualResponse.getItemId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("이름으로 검색하기-찾기 불가")
    void t4() throws Exception {
        //given
        given(itemService.findByName("물건1")).willThrow(new EntityNotFoundException("조회할 수 없습니다."));

        //when
        ErrorResponse actualResponse = getResponseBody(mockMvc.perform(get("/item/search").param("itemName", "물건1")), ErrorResponse.class);
        //then
        assertThat(actualResponse.getMessage()).isEqualTo("조회할 수 없습니다.");
    }


    private <T> T getResponseBody(ResultActions action, Class<T> responseType) throws JsonProcessingException, UnsupportedEncodingException {
        final String resultString = action.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(resultString, responseType);
    }
}