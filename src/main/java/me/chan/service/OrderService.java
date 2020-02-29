package me.chan.service;

import me.chan.domain.FlashSaleOrder;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.vo.GoodsVO;

public interface OrderService {

    OrderInfo createOrderInfo(GoodsVO goods, User user);

    OrderInfo getOrderInfoById(Long id);

    FlashSaleOrder getFSOrderByGoodsIdAndUserId(Long goodsId, Long userId);
}
