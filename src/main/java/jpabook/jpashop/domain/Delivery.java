package jpabook.jpashop.domain;

import lombok.Getter;

import jakarta.persistence.*;

@Embeddable
@Getter
public class Delivery {

    protected Delivery(){}

    public Delivery(Address address) {
        this.address = address;
        status = DeliveryStatus.READY;
    }

    @Embedded
    private Address address;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus status;
}
