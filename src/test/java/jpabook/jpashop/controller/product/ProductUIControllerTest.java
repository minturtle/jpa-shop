package jpabook.jpashop.controller.product;

import jpabook.jpashop.controller.api.common.response.CategoryResponse;
import jpabook.jpashop.domain.product.Category;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.testUtils.TestDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.Map;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductUIControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void 사용자는_상품_리스트페이지_접속시_상품_리스트_페이지를_반환받을수_있다() throws Exception{
        //given
        Category givenAlbumCategory = albumCategory;
        Category givenBookCategory = bookCategory;
        Category givenMovieCategory = movieCategory;

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        ModelMap modelMap = mvcResponse.getModelAndView().getModelMap();

        assertThat(modelMap).isNotNull();
        assertThat(modelMap.containsKey("categories")).isTrue();

        Map<ProductType, CategoryResponse.ListInfo> categories = (Map<ProductType, CategoryResponse.ListInfo>) modelMap.get("categories");
        assertThat(categories.keySet()).contains(ProductType.ALBUM, ProductType.MOVIE, ProductType.BOOK);

        assertAll(
                ()-> assertThat(categories.get(ProductType.ALBUM).getCategories()).extracting("uid" ,"name").containsExactly(tuple(givenAlbumCategory.getUid(), givenAlbumCategory.getName())),
                ()-> assertThat(categories.get(ProductType.MOVIE).getCategories()).extracting("uid" ,"name").containsExactly(tuple(givenMovieCategory.getUid(), givenMovieCategory.getName())),
                ()-> assertThat(categories.get(ProductType.BOOK).getCategories()).extracting("uid" ,"name").containsExactly(tuple(givenBookCategory.getUid(), givenBookCategory.getName()))
        );

    }



}