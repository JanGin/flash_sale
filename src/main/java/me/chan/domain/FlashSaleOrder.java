package me.chan.domain;

import lombok.Data;

@Data
public class FlashSaleOrder {

    private Long id;

    private Long orderId;

    private Long goodsId;

    private Long  userId;
}
