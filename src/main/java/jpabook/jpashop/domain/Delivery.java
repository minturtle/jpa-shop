package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Delivery {

    protected Delivery(){}

    public Delivery(Address address) {
        this.address = address;
        status = DeliveryStatus.READY;
    }

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @Embedded
    private Address address;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus status;
}
