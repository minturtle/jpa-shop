package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Getter @Setter
public class OrderItemListDto {

    private List<OrderItemDto> items = new ArrayList<>();

    public int size(){ return items.size();}

    public Iterator<OrderItemDto> iterator(){
        return items.iterator();
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto{
        private Long itemId;
        private int count;
    }
}
