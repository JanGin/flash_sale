package me.chan.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.RedisKeyPrefix;
import me.chan.domain.FlashSaleGoods;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.service.FlashSaleService;
import me.chan.service.GoodsService;
import me.chan.service.OrderService;
import me.chan.service.StringRedisService;
import me.chan.util.MD5Util;
import me.chan.util.UUIDUtil;
import me.chan.vo.GoodsVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service("flashSaleService")
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {


    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisService strRedisService;

    @Transactional
    @Override
    public OrderInfo doFlashSale(GoodsVO goods, User user) {
        FlashSaleGoods g = new FlashSaleGoods();
        g.setId(goods.getId());
        goodsService.updateGoodsStock(g);

        OrderInfo info = orderService.createOrderInfo(goods, user);
        return info;
    }

    @Override
    public String generateFsPath(User user, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.generate()+ RandomStringUtils.randomNumeric(4));
        String redisKey = RedisKeyPrefix.GOODSPATH_CACHE + user.getId() + "_" + goodsId;
        strRedisService.set(redisKey, path, 1L, TimeUnit.MINUTES);
        return path;
    }

    @Override
    public boolean validFlashSalePath(User user, Long goodsId, String path) {

        String redisKey = RedisKeyPrefix.GOODSPATH_CACHE + user.getId() + "_" + goodsId;
        String original = strRedisService.get(redisKey);
        if (StringUtils.equals(original, path)) {
            strRedisService.delete(redisKey);
            return true;
        }

        return false;
    }

}
