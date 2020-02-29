package me.chan.service.impl;

import me.chan.domain.FlashSaleGoods;
import me.chan.domain.Goods;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.service.FlashSaleService;
import me.chan.service.GoodsService;
import me.chan.service.OrderService;
import me.chan.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("flashSaleService")
public class FlashSaleServiceImpl implements FlashSaleService {


    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Transactional
    @Override
    public OrderInfo doFlashSale(GoodsVO goods, User user) {
        FlashSaleGoods g = new FlashSaleGoods();
        g.setId(goods.getId());
        goodsService.updateGoodsStock(g);

        OrderInfo info = orderService.createOrderInfo(goods, user);
        return info;
    }
}
