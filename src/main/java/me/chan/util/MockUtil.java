package me.chan.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chan.domain.User;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MockUtil {

    private static void createUser(int count) throws Exception{
        List<User> users = new ArrayList<>(count);

        //插入数据库
        Connection conn = DBUtil.getConn();
        String sql = "select * from user where id > 1";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getLong(1));
            user.setMobile(rs.getString(2));
            user.setPassword(rs.getString(3));
            user.setSalt(rs.getString(4));
            user.setLoginCount(rs.getInt(8));
            System.out.println(user);
            users.add(user);
        }
        pstmt.close();
        conn.close();
        //登录，生成token
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("D:/tokens.txt");
        if(file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i=0,size=users.size(); i< size; i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getMobile()+"&password="+MD5Util.inputPwdToFormPwd("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0 ,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");
            log.info("{} user generates token is {}:", user.getMobile(), token);

            String row = user.getId()+","+token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();

        System.out.println("over");
    }

    public static void main(String[] args)throws Exception {
        createUser(5000);
    }
}
