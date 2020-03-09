package me.chan.service;

import me.chan.domain.OrderInfo;
import me.chan.domain.User;
import me.chan.vo.GoodsVO;

public interface FlashSaleService {

    OrderInfo doFlashSale(GoodsVO goods, User user);

    String generateFsPath(User user, Long goodsId);

    boolean validFlashSalePath(User user, Long goodsId, String path);

    String createBase64VerifyImg(User user, Long goodsIds);
}
