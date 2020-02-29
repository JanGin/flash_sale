package me.chan.service;

import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.vo.GoodsVO;

public interface FlashSaleService {

    OrderInfo doFlashSale(GoodsVO goods, User user);
}
