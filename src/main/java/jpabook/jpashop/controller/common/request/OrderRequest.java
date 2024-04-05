package jpabook.jpashop.controller.common.request;

import lombok.Getter;

import java.util.List;

public abstract class OrderRequest {

    @Getter
    public class ItemInfoList{
        List<ItemInfo> items;
    }


    @Getter
    public class ItemInfo{
        private String itemId;
        private int quantity;
    }

}
