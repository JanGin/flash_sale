package me.chan.dao;


import me.chan.domain.FlashSaleGoods;
import me.chan.vo.GoodsVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao {

    @Select("SELECT b.*, a.sale_price, a.start_date, a.end_date, a.stock_count FROM flash_sale_goods a " +
            "LEFT JOIN goods b ON a.goods_id = b.id")
    @Results({
            @Result(column = "sale_price", property = "salePrice"),
            @Result(column = "start_date", property = "startDate"),
            @Result(column = "end_date", property = "endDate"),
            @Result(column = "goods_name", property = "goodsName"),
            @Result(column = "goods_title", property = "goodsTitle"),
            @Result(column = "goods_price", property = "goodsPrice"),
            @Result(column = "stock_count", property = "stockCount"),
            @Result(column = "goods_stock", property = "goodsStock"),
            @Result(column = "goods_img", property = "goodsImg")
    })
    List<GoodsVO> getGoodsList();

    @Select("SELECT b.*, a.sale_price, a.start_date, a.end_date, a.stock_count FROM flash_sale_goods a " +
            "LEFT JOIN goods b ON a.goods_id = b.id WHERE b.id = #{id}")
    @Results({
            @Result(column = "sale_price", property = "salePrice"),
            @Result(column = "start_date", property = "startDate"),
            @Result(column = "end_date", property = "endDate"),
            @Result(column = "goods_name", property = "goodsName"),
            @Result(column = "goods_title", property = "goodsTitle"),
            @Result(column = "goods_price", property = "goodsPrice"),
            @Result(column = "stock_count", property = "stockCount"),
            @Result(column = "goods_stock", property = "goodsStock"),
            @Result(column = "goods_img", property = "goodsImg"),
            @Result(column = "goods_detail", property = "goodsDetail")
    })
    GoodsVO getGoodsById(@Param("id") Long id);

    @Update("UPDATE flash_sale_goods g SET stock_count = g.stock_count - 1 WHERE g.goods_id = #{g.id} AND g.stock_count > 0")
    void updateGoodsStock(@Param("g") FlashSaleGoods goods);
}
