package me.chan.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.domain.FlashSaleGoods;
import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.exception.GlobalException;
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
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
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

    private static final String IMAGE_SUFFIX = "JPEG";

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


    @Override
    public String createBase64VerifyImg(User user, Long goodsId) {

        int width = 90;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        String verifyPic = verifyCode + "=";
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyPic, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int res = calc(verifyCode);
        strRedisService.set( user.getId()+","+goodsId, String.valueOf(res),
                        60L, TimeUnit.SECONDS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, IMAGE_SUFFIX, baos);
            byte[] imageByte = baos.toByteArray();
            return new BASE64Encoder().encode(imageByte).trim();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
    }

    private static int calc(String code) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("JavaScript");
        try {
            int res = (Integer) se.eval(code);
            return res;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return -1;
        }
    }

    private String generateVerifyCode(Random rdm) {
        char[] ops = new char[] {'+', '*'};
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(2)];
        char op2 = ops[rdm.nextInt(2)];
        StringBuffer exp = new StringBuffer();
        exp.append(num1).append(op1).append(num2).append(op2).append(num3);
        return exp.toString();
    }
}
