package jpabook.jpashop.domain.item;

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


    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected abstract void updateInheritedValues(Item item);

    public void update(Item item){
        this.name = item.getName();
        this.stockQuantity = item.getStockQuantity();
        this.price = item.getPrice();
        updateInheritedValues(item);
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
}
