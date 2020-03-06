package me.chan.common;

import lombok.Data;
import me.chan.domain.User;

import java.io.Serializable;

@Data
public class FlashsaleMsg implements Serializable {

    private Long goodsId;
    private User user;
}
