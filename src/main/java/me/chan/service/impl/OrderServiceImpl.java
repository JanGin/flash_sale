package me.chan.service.impl;

import me.chan.dao.OrderDao;
import me.chan.domain.FlashSaleOrder;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.service.OrderService;
import me.chan.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("orderService")
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderDao orderDao;

    @Transactional
    @Override
    public OrderInfo createOrderInfo(GoodsVO goods, User user) {
        OrderInfo order = new OrderInfo();
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setGoodsCount(1);
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(goods.getSalePrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setUserId(user.getId());
        orderDao.insertOrder(order);
        FlashSaleOrder fsOrder = new FlashSaleOrder();
        fsOrder.setGoodsId(goods.getId());
        fsOrder.setOrderId(order.getId());
        fsOrder.setUserId(user.getId());
        orderDao.insertFlashOrder(fsOrder);
        return order;
    }

    @Override
    public OrderInfo getOrderInfoById(Long id) {
        return orderDao.getOrderInfoById(id);
    }

    @Override
    public FlashSaleOrder getFSOrderByGoodsIdAndUserId(Long goodsId, Long userId) {
        return orderDao.getFSOrderByGoodsIdAndUserId(goodsId, userId);
    }
}
