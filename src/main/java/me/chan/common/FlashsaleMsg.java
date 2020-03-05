package me.chan.common;

import lombok.Data;
import me.chan.domain.User;

@Data
public class FlashsaleMsg {

    private Long goodsId;
    private User user;
}
