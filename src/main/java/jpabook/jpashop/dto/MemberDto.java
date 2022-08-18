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
        return Member.createMember(username, userId, password, address.getCity(), address.getStreet(), address.getZipcode());
    }

    private String userId;
    private String password;
    private String username;
    private Address address;

}
