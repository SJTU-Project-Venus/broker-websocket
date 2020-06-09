package macoredroid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "finishedOrder")
public class OrderListener {
    Logger logger = LoggerFactory.getLogger(MarketDepthDTOListener.class);
    @RabbitHandler
    public void handle(String message){
        logger.info("finishedOrder: {}", message);
        try {
            WebSocket.sendMessage(message,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
