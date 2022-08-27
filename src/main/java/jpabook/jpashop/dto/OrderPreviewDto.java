package jpabook.jpashop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPreviewDto {

    private Long orderId;

    private String title;

    private int totalPrice;

}
