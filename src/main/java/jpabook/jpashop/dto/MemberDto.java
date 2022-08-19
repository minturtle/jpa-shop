package jpabook.jpashop.dto;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    public MemberDto(String userId, String password, String username, String city, String street, String zipcode) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.address = new Address(city, street, zipcode);
    }



    public Member toMember(){
        return Member.createMember(username, userId, password, address.getCity(), address.getStreet(), address.getZipcode(), true);
    }

    private String userId;
    private String password;
    private String username;
    private Address address;


    public static class MemberDtoBuilder{

        public MemberDtoBuilder userIdAndPassword(String userId, String password){
            this.userId = userId;
            this.password = password;
            return this;
        }

        public MemberDtoBuilder username(String username){
            this.username = username;
            return this;
        }
        public MemberDtoBuilder address(String city, String street, String zipcode){
            this.address = new Address(city,street,zipcode);
            return this;
        }

        public MemberDtoBuilder address(Address address){
            this.address = address;
            return this;
        }
        public MemberDto build(){
            return new MemberDto(userId, password, username, address.getCity(), address.getStreet(), address.getZipcode());
        }
        private String userId = "";
        private String password = "";
        private String username = "";
        private Address address;

    }
}

