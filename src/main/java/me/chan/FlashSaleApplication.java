package me.chan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.chan.dao")
public class FlashSaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashSaleApplication.class, args);
    }

}
