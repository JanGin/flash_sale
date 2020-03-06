package me.chan.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FlashSaleOrder {

    private Long id;

    private Long orderId;

    private Long goodsId;

    private Long  userId;

    private Date createTime;
}
