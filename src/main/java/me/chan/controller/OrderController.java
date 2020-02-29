package me.chan.controller;

import me.chan.common.CodeMsg;
import me.chan.common.Result;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.service.GoodsService;
import me.chan.service.OrderService;
import me.chan.vo.GoodsVO;
import me.chan.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @ResponseBody
    @GetMapping("/detail/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable("id") Long id, User user) {
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo order = orderService.getOrderInfoById(id);
        if (null == order) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = order.getGoodsId();
        GoodsVO goods = goodsService.getGoodsById(goodsId);
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
