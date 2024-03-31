package jpabook.jpashop.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class MemberResponse {



    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Detail{
        private String name;
        private String address;
    }

}
