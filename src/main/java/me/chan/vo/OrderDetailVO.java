package me.chan.vo;

import lombok.Data;
import me.chan.domain.OrderInfo;

@Data
public class OrderDetailVO {

    private GoodsVO goods;
    private OrderInfo order;
}
