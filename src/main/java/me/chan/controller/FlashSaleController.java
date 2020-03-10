package me.chan.controller;

import com.alibaba.fastjson.JSON;
import me.chan.common.CodeMsg;
import me.chan.common.FlashsaleMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.common.Result;
import me.chan.domain.FlashSaleOrder;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.mq.MessageSender;
import me.chan.service.*;
import me.chan.vo.GoodsVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private VerifyCodeService vcService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

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

   /* @PostMapping("/do_sale")
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
    }*/


    @PostMapping("/{path}/do_sale")
    @ResponseBody
    public Result<Long> doFlashSale(User user, @RequestParam("goodsId")Long goodsId,
                                    @RequestParam("code")String code,   @PathVariable("path")String path) {
        if (null == user) {
           return Result.error(CodeMsg.SESSION_ERROR);
        }

        String redisKey = path;
        boolean isPass = fsService.validFlashSalePath(user, goodsId, redisKey);
        if (!isPass) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        boolean isCorrect = vcService.checkVerifyCode(user.getId(), goodsId, code);
        if (!isCorrect) {
            return Result.error(CodeMsg.VERIFY_CODE_FAILED);
        }

        //减少对redis的访问
        boolean isOver = localOverMap.get(goodsId);
        if (isOver) {
            return Result.error(CodeMsg.SALE_ACTIVITY_OVER);
        }

        //预减库存
        long stock = redisService.decr(RedisKeyPrefix.GOODSLIST_CACHE+goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SALE_ACTIVITY_OVER);
        }

        FlashSaleOrder order = orderService.getFSOrderByGoodsIdAndUserId(goodsId, user.getId());
        if(order != null) {
           return Result.error(CodeMsg.SALE_REPEAT_FORBIDDEN);
        }
        FlashsaleMsg fsmsg = new FlashsaleMsg();
        fsmsg.setGoodsId(goodsId);
        fsmsg.setUser(user);
        sender.sendFlashSaleMsg(JSON.toJSONString(fsmsg));
        //入队成功，立即返回
        return Result.success(1L);
    }

    @ResponseBody
    @GetMapping("/path")
    public Result<String> path(User user, @RequestParam("goodsId") Long goodsId) {
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        String path = fsService.generateFsPath(user, goodsId);
        return Result.success(path);
    }


    /**
     * 输出base64编码的图片
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> verifyCode(User user, @RequestParam("goodsId")Long goodsId) {
        /**
        if(null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (!localOverMap.get(goodsId)) {
            return Result.error(CodeMsg.SALE_PRODUCT_SELLOUT);
        }
         **/
        String base64Img = vcService.createBase64VerifyImg(user, goodsId);
        return Result.success(base64Img);
    }


    @GetMapping("/result")
    @ResponseBody
    public Result<Long> flashSaleResult(User user, @RequestParam("goodsId")Long goodsId) {
        FlashSaleOrder order = orderService.getFSOrderByGoodsIdAndUserId(goodsId, user.getId());
        if (Objects.nonNull(order)) {
            return Result.success(order.getOrderId());
        }

        //继续排队
        return Result.success(1L);
    }


    @Override
    public void afterPropertiesSet() {

        List<GoodsVO> list = goodsService.getGoodsList();
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().forEach((e)->{
                redisService.set(RedisKeyPrefix.GOODSLIST_CACHE+e.getId(), e.getStockCount());
                localOverMap.put(e.getId(), false);
            });
        }
    }
}
