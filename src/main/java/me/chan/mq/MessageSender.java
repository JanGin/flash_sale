package me.chan.mq;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.GlobalConstant;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageSender {


   @Autowired
   private AmqpTemplate amqpTemplate ;

    public void send(String message) {
        log.info("sending message:{}", message);
        amqpTemplate.convertAndSend(GlobalConstant.QUEUE_NAME, message);
    }
}
