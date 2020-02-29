package me.chan.dao;

import me.chan.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(@Param("id") Integer id);

    @Insert("INSERT INTO user (id, name) VALUES (#{user.id}, #{user.name})")
    User addUser(@Param("user")User user);

    @Select("SELECT u.id, u.mobile, u.login_count FROM user u " +
            "WHERE u.mobile=#{mobile} AND u.password = #{password}")
    User getUserByMobileAndPassword(@Param("mobile") String mobile, @Param("password") String password);

    @Update("UPDATE user u SET u.last_login_date=now(), " +
            "u.login_count=#{user.loginCount} " +
            "WHERE u.mobile=#{user.mobile} AND u.password=#{user.password}")
    void saveUser(@Param("user") User user);

    @Select("SELECT u.id, u.mobile, u.password, u.salt, u.login_count loginCount FROM user u WHERE u.mobile = #{mobile}")
    User getUserByMobile(@Param("mobile") String mobile);
}
