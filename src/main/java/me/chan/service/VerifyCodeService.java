package me.chan.service;

import me.chan.domain.User;

public interface VerifyCodeService {

    String createBase64VerifyImg(User user, Long goodsId);

    boolean checkVerifyCode(Long userId, Long goodsId, String code);
}
