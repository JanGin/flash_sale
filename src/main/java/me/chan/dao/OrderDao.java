package me.chan.dao;


import me.chan.domain.FlashSaleOrder;
import me.chan.domain.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {


    @Insert("INSERT INTO order_info (user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)" +
            " VALUES (#{order.userId}, #{order.goodsId}, #{order.goodsName}, #{order.goodsCount}, #{order.goodsPrice}, " +
            "#{order.orderChannel}, #{order.status}, #{order.createDate})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Long insertOrder(@Param("order") OrderInfo order);

    @Insert("INSERT INTO flash_sale_order (user_id, goods_id, order_id, create_time) " +
            "VALUES (#{order.userId}, #{order.goodsId}, #{order.orderId}, now())")
    @SelectKey(keyProperty = "order.id", keyColumn = "order.id", resultType = Long.class, before = false, statement = "SELECT LAST_INSERT_ID()")
    Long insertFlashOrder(@Param("order") FlashSaleOrder fsOrder);

    @Select("SELECT * FROM order_info WHERE id = #{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "goods_id", property = "goodsId"),
            @Result(column = "goods_name", property = "goodsName"),
            @Result(column = "goods_count", property = "goodsCount"),
            @Result(column = "goods_price", property = "goodsPrice"),
            @Result(column = "order_channel", property = "orderChannel"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_date", property = "createDate"),
    })
    OrderInfo getOrderInfoById(@Param("id") Long id);

    @Select("SELECT * FROM flash_sale_order WHERE goods_id = #{goodsId} AND user_id = #{userId}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "goods_id", property = "goodsId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "order_id", property = "orderId"),
            @Result(column = "create_time", property = "createTime")
    })
    FlashSaleOrder getFSOrderByGoodsIdAndUserId(@Param("goodsId") Long goodsId, @Param("userId") Long userId);
}
