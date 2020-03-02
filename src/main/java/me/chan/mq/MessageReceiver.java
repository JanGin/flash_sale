package me.chan.mq;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.GlobalConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageReceiver {


    @RabbitListener(queues={GlobalConstant.QUEUE_NAME})
    public void receive(String message) {
        log.info("receive message:{}", message);

    }
}
