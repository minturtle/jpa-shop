package jpabook.jpashop.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressInfo {


    private String address;
    private String detailedAddress;


    public AddressInfo(AddressInfo addressInfo) {
        this.address = addressInfo.getAddress();
        this.detailedAddress = addressInfo.getDetailedAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressInfo that = (AddressInfo) o;
        return Objects.equals(address, that.address) && Objects.equals(detailedAddress, that.detailedAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, detailedAddress);
    }
}
