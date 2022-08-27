package jpabook.jpashop.controller;

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
            itemDto1 = new ItemDto.ItemDtoBuilder().putItemId(1L).setItemType(Item.class).putItemField("물건1", 15000 , 30).build();
            itemDto2 = new ItemDto.ItemDtoBuilder().putItemId(2L).setItemType(Item.class).putItemField("물건2", 30000, 20).build();
    }

    @Test
    @DisplayName("아이템 리스트 조회하기")
    void t1() throws Exception {
        //given
        given(itemService.findAll(1, ItemService.SortType.최신순)).willReturn(List.of(itemDto1, itemDto2));
        List<ItemDto.ItemPreviewDto> expectResult = List.of(new ItemDto.ItemPreviewDto(1L, "물건1", 15000)
                , new ItemDto.ItemPreviewDto(2L, "물건2", 30000));
        //when
        String resultString = mockMvc.perform(get("/item/items").param("page", "1"))
                .andReturn().getResponse().getContentAsString();
        List<ItemDto.ItemPreviewDto> actualResult = List.of(objectMapper.readValue(resultString, ItemDto.ItemPreviewDto[].class));
        //then
        assertThat(actualResult).containsAll(expectResult);
        assertThat(expectResult).containsAll(actualResult);
    }
}