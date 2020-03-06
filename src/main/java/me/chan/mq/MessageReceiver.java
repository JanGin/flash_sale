package me.chan.mq;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.FlashsaleMsg;
import me.chan.common.GlobalConstant;
import me.chan.domain.FlashSaleOrder;
import me.chan.domain.User;
import me.chan.service.FlashSaleService;
import me.chan.service.GoodsService;
import me.chan.service.OrderService;
import me.chan.util.JSONUtil;
import me.chan.vo.GoodsVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static me.chan.config.RabbitmqConfig.FLASHSALE_QUEUE;

@Component
@Slf4j
public class MessageReceiver {

    @Autowired
    private OrderService orderService;

    @Autowired
    private FlashSaleService fsService;

    @Autowired
    private GoodsService goodsService;

    @RabbitListener(queues={GlobalConstant.QUEUE_NAME})
    public void receive(String message) {
        log.info("receive message:{}", message);
    }


    @RabbitListener(queues = FLASHSALE_QUEUE)
    public void receiveFSMsg(String message) {
        log.info("[the consumer receives message]:{}", message);
        FlashsaleMsg fsmsg = JSONUtil.parseJSONStr2Obj(message, FlashsaleMsg.class);
        long goodsId = fsmsg.getGoodsId();
        User user = fsmsg.getUser();
        GoodsVO goods = goodsService.getGoodsById(goodsId);
        if (null == goods || goods.getStockCount() <= 0) {
           return ;
        }

        FlashSaleOrder order = orderService.getFSOrderByGoodsIdAndUserId(goodsId, user.getId());
        if (order != null) {
            return ;
        }

        fsService.doFlashSale(goods, user);
    }
}
