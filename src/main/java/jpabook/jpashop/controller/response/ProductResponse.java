package jpabook.jpashop.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ProductResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ProductResponsePreview")
    public static class Preview {
        private String productUid;
        private String productName;
        private int price;
        private String productImage;
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static abstract class Detail {

        private String uid;
        private String name;
        private String description;
        private int price;
        private int stockQuantity;
        private String thumbnailUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "MovieDetailInfo")
    public static class MovieDetail extends Detail{
        private String director;
        private String actor;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "AlbumDetailInfo")
    public static class AlbumDetail extends Detail{
        private String artist;
        private String etc;
    }
}
