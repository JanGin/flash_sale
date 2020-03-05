package me.chan.config;

import me.chan.common.GlobalConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {


    public static final String FLASHSALE_QUEUE = "flashsale";


    @Bean
    public Queue queue() {
        return new Queue(GlobalConstant.QUEUE_NAME, true);
    }


    @Bean
    public Queue flashsaleQueue() {
        return new Queue(FLASHSALE_QUEUE, true);
    }
}
