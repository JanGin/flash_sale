package me.chan.service.impl;

import me.chan.dao.GoodsDao;
import me.chan.domain.FlashSaleGoods;
import me.chan.service.GoodsService;
import me.chan.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("goodsServiceI")
public class GoodsServiceImpl implements GoodsService {


    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<GoodsVO> getGoodsList() {
        return goodsDao.getGoodsList();
    }

    @Override
    public GoodsVO getGoodsById(Long id) {
        return goodsDao.getGoodsById(id);
    }

    @Override
    public void updateGoodsStock(FlashSaleGoods g) {
        goodsDao.updateGoodsStock(g);
    }
}
