package me.chan.config;

import me.chan.common.GlobalConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {


    @Bean
    public Queue queue() {
        return new Queue(GlobalConstant.QUEUE_NAME, true);
    }
}
