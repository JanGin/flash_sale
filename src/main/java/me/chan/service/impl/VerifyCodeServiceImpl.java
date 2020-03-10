package me.chan.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.domain.User;
import me.chan.exception.GlobalException;
import me.chan.service.StringRedisService;
import me.chan.service.VerifyCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


@Service("verifyCodeService")
@Slf4j
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private static final String IMAGE_SUFFIX = "JPEG";

    @Autowired
    private StringRedisService strRedisService;

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

        //用户登陆态才缓存验证码结果
        if (user != null) {
            //把验证码存到redis中
            int res = calc(verifyCode);
            String cacheKey = RedisKeyPrefix.VERIFY_CODE + user.getId() + "_" + goodsId;
            strRedisService.set(cacheKey, String.valueOf(res),
                    90L, TimeUnit.SECONDS);
        }

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

    @Override
    public boolean checkVerifyCode(Long userId, Long goodsId, String code) {

        String cacheKey = RedisKeyPrefix.VERIFY_CODE + userId + "_" + goodsId;
        String cacheCode = strRedisService.get(cacheKey);
        if (StringUtils.isBlank(cacheCode) || !StringUtils.equals(cacheCode, code)) {
            return false;
        }
        return true;
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
