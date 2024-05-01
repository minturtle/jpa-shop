package jpabook.jpashop.controller.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jpabook.jpashop.enums.product.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public abstract class CategoryResponse {



    @Schema(name = "CategoryResult")
    @Getter
    @NoArgsConstructor
    public static class ListResult {
        private List<ListInfo> data = new ArrayList<>();

        public void add(ProductType productType){
            data.add(new ListInfo(productType));
        }

        public ListInfo get(ProductType productType){
            return data.stream()
                    .filter(info -> info.getProductType().equals(productType))
                    .findFirst()
                    .orElse(null);
        }

    }


    @Schema(name = "CategoryListInfo")
    @Data
    @NoArgsConstructor
    public static class ListInfo {
        public ListInfo(ProductType productType) {
            this.productType = productType;
            this.categories = new ArrayList<>();
        }

        private ProductType productType;
        private List<Info> categories;

        public void addCategory(String uid, String name){
            categories.add(new Info(uid, name));
        }

    }

    @Schema(name = "CategoryInfo")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info{
        private String uid;
        private String name;
    }


}
