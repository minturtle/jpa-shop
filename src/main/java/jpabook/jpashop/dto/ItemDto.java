package jpabook.jpashop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public abstract class ItemDto {


    @Data
    @AllArgsConstructor
    @Builder
    public static class Detail{

    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class Preview{

    }

}
