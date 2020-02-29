package me.chan.service;

import me.chan.domain.FlashSaleGoods;
import me.chan.vo.GoodsVO;

import java.util.List;

public interface GoodsService {

    List<GoodsVO> getGoodsList();

    GoodsVO getGoodsById(Long id);

    void updateGoodsStock(FlashSaleGoods g);
}
