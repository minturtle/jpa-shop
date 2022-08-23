package jpabook.jpashop.domain.item;

import jpabook.jpashop.dto.ItemDto;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Inheritance
public abstract class Item {
    protected Item(){}

    public Item(int stockQuantity, int price, String name) {
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.name = name;
    }

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private int stockQuantity;
    private int price;
    private String name;

    public void update(ItemDto item){
        this.name = item.getName();
        this.stockQuantity = item.getStockQuantity();
        this.price = item.getPrice();
        updateInheritedFields(item);
    }


    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity)throws IllegalArgumentException{
        checkIsOrderQuantityBiggerThanStock(quantity); //주문양이 재고보다 많으면 예외발생
        this.stockQuantity -= quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected abstract void updateInheritedFields(ItemDto item);

    private void checkIsOrderQuantityBiggerThanStock(int quantity) {
        if(stockQuantity < quantity) throw new IllegalArgumentException("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }
}
