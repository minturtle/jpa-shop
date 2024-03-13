package jpabook.jpashop.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public abstract class MemberRequest{



    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Create{


    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Update {
        private String name;
        private String password;
    }



    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class Login {
        private String userId;
        private String password;

    }
}



