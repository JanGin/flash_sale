package me.chan.controller;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.common.Result;
import me.chan.domain.User;
import me.chan.service.GoodsService;
import me.chan.service.StringRedisService;
import me.chan.service.UserService;
import me.chan.vo.GoodsDetailVO;
import me.chan.vo.GoodsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ThymeleafViewResolver resolver;

    @Autowired
    private StringRedisService redisService;


    /*@RequestMapping("/to_list")
    public String listPage(@CookieValue(value = "token", required = false)String cookieToken,
                           @RequestParam(value = "token", required = false) String urlToken,
                           HttpServletResponse response,
                           Model model) {

        if (StringUtils.isBlank(cookieToken) || StringUtils.isBlank(urlToken))
            return "login";

        String token = StringUtils.isEmpty(cookieToken)?urlToken:cookieToken;
        User user = userService.getUserByToken(RedisKeyPrefix.USER_KEY+token, response);
        model.addAttribute("user", user);
        return "goods_list";

    }*/

    /**
     * 未使用页面缓存前
     * QPS:1341
     * 1000 threads 20 times
     * 使用页面缓存后
     * QPS:3161
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String listPage(Model model, User user,
                           HttpServletRequest req,
                           HttpServletResponse resp) {

        String html = redisService.get(RedisKeyPrefix.GOODSLIST_HTML_CACHE);
        if (StringUtils.isNotBlank(html)) {
            return html;
        }

        List<GoodsVO> goodsList =  goodsService.getGoodsList();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);

        WebContext ctx = new WebContext(req, resp, req.getServletContext(), req.getLocale(), model.asMap());
        html = resolver.getTemplateEngine().process("goods_list", ctx);
        if (StringUtils.isNotBlank(html)) {
            redisService.set(RedisKeyPrefix.GOODSLIST_HTML_CACHE, html, 60L, TimeUnit.SECONDS);
        }
        return html;
    }


    @RequestMapping("/detail2/{id}")
    @ResponseBody
    public String goodsDetail2(Model model, User user,
                            @PathVariable("id") Long id,
                              HttpServletRequest req,
                              HttpServletResponse resp) {
        String goodsId = String.valueOf(id);
        if ("null".equals(goodsId) || StringUtils.isBlank(goodsId)
                || !StringUtils.isNumeric(goodsId)) {
            return "_404";
        }

        String html = redisService.get(RedisKeyPrefix.GOODSID_HTML_CACHE + goodsId);
        if (StringUtils.isNotBlank(html)) {
            return html;
        }

        GoodsVO goodsVO = goodsService.getGoodsById(id);
        if (null == goodsVO) {
            return "_404";
        }
        int saleStatus = 0, remainSeconds = 0;
        long currentTime = System.currentTimeMillis();
        long startTime = goodsVO.getStartDate().getTime();
        long endTime = goodsVO.getEndDate().getTime();
        log.info("start:{}, current:{}, there is {} time left...", startTime, currentTime, (currentTime - startTime));
        if (startTime > currentTime) {
            remainSeconds = (int)((startTime - currentTime) / 1000);
        } else if (currentTime > endTime) {
            saleStatus = 2;
            remainSeconds = -1;
        } else if (startTime < currentTime && currentTime < endTime) {
            saleStatus = 1;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("saleStatus", saleStatus);
        model.addAttribute("goods", goodsVO);
        model.addAttribute("user", user);

        WebContext ctx = new WebContext(req, resp, req.getServletContext(), req.getLocale(), model.asMap());
        html = resolver.getTemplateEngine().process("goods_detail", ctx);
        if (StringUtils.isNotBlank(html)) {
            redisService.set(RedisKeyPrefix.GOODSID_HTML_CACHE+goodsId, html, 60L, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping("/detail/{id}")
    @ResponseBody
    public Result<GoodsDetailVO> goodsDetail(Model model, User user,
                               @PathVariable("id") Long id) {
        String goodsId = String.valueOf(id);
        if ("null".equals(goodsId) || StringUtils.isBlank(goodsId)
                || !StringUtils.isNumeric(goodsId)) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        GoodsVO goodsVO = goodsService.getGoodsById(id);
        if (null == goodsVO) {
            return Result.error(CodeMsg.SALE_PRODUCT_NOT_EXIST);
        }

        GoodsDetailVO goodsDetail = new GoodsDetailVO();
        int saleStatus = 0, remainSeconds = 0;
        long currentTime = System.currentTimeMillis();
        long startTime = goodsVO.getStartDate().getTime();
        long endTime = goodsVO.getEndDate().getTime();
        if (startTime > currentTime) {
            remainSeconds = (int)((startTime - currentTime) / 1000);
        } else if (currentTime > endTime) {
            saleStatus = 2;
            remainSeconds = -1;
        } else if (startTime < currentTime && currentTime < endTime) {
            saleStatus = 1;
        }
        goodsDetail.setGoodsVO(goodsVO);
        goodsDetail.setRemainSeconds(remainSeconds);
        goodsDetail.setSaleStatus(saleStatus);
        goodsDetail.setUser(user);

        return Result.success(goodsDetail);
    }

}
