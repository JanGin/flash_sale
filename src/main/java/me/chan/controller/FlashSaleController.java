package me.chan.controller;

import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.common.Result;
import me.chan.domain.FlashSaleOrder;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.service.FlashSaleService;
import me.chan.service.GoodsService;
import me.chan.service.OrderService;
import me.chan.service.RedisService;
import me.chan.vo.GoodsVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/fs")
@Controller
public class FlashSaleController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FlashSaleService fsService;

    @Autowired
    private RedisService redisService;

    /**
     * QPS : 616
     * 5000 threads 10 times
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/do_sale2")
    public String doFlashSale2(Model model, User user, @RequestParam("goodsId") Long goodsId) {

        if (null == user) {
            return "login";
        }

        GoodsVO goods = goodsService.getGoodsById(goodsId);
        if (null == goods || goods.getStockCount() <= 0) {
            model.addAttribute("errmsg", CodeMsg.SALE_ACTIVITY_OVER.getMessage());
            return "sale_failed";
        }

        FlashSaleOrder order = orderService.getFSOrderByGoodsIdAndUserId(goodsId, user.getId());
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.SALE_REPEAT_FORBIDDEN);
            return "sale_failed";
        }

        OrderInfo orderInfo = fsService.doFlashSale(goods, user);
        model.addAttribute("goods", goods);
        model.addAttribute("order", orderInfo);
        return "order_detail";
    }

    @PostMapping("/do_sale")
    @ResponseBody
    public Result<OrderInfo> doFlashSale(Model model, User user, @RequestParam("goodsId") Long goodsId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        GoodsVO goods = goodsService.getGoodsById(goodsId);
        if (null == goods || goods.getStockCount() <= 0) {
            return Result.error(CodeMsg.SALE_PRODUCT_NOT_EXIST);
        }

        FlashSaleOrder order = orderService.getFSOrderByGoodsIdAndUserId(goodsId, user.getId());
        if (order != null) {
            return Result.error(CodeMsg.SALE_REPEAT_FORBIDDEN);
        }

        OrderInfo orderInfo = fsService.doFlashSale(goods, user);
        if (null == orderInfo) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        return Result.success(orderInfo);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        List<GoodsVO> list = goodsService.getGoodsList();
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().forEach((e)->{
                redisService.set(RedisKeyPrefix.GOODSLIST_CACHE+e.getId(), e.getStockCount());
            });
        }
    }
}
