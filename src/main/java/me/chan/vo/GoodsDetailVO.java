package me.chan.vo;

import lombok.Data;
import me.chan.domain.User;

@Data
public class GoodsDetailVO {

    private GoodsVO goodsVO;
    private int remainSeconds;
    private int saleStatus;
    private User user;
}
