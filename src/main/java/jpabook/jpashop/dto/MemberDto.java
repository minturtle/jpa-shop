package jpabook.jpashop.dto;

import jpabook.jpashop.domain.Address;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    public MemberDto(Long id, String userId, String password, String username, String city, String street, String zipcode) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.address = new Address(city, street, zipcode);
    }

    private Long id;
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
            this.city = city;
            this.street = street;
            this.zipcode = zipcode;
            return this;
        }

        public MemberDtoBuilder address(Address address){
            address(address.getCity(), address.getStreet(), address.getZipcode());
            return this;
        }

        public MemberDtoBuilder id(Long id){
            this.id = id;
            return this;
        }

        public MemberDto build(){
            return new MemberDto(id, userId, password, username, city,street,zipcode);
        }
        private Long id;
        private String userId = "";
        private String password = "";
        private String username = "";
        private String city = "";
        private String street = "";
        private String zipcode = "";

    }
}

