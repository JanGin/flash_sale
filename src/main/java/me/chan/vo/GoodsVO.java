package me.chan.vo;

import lombok.Data;
import me.chan.domain.Goods;

import java.util.Date;

@Data
public class GoodsVO extends Goods {

    private Date startDate;

    private Date endDate;

    private Double salePrice;

    private Integer stockCount;
}
