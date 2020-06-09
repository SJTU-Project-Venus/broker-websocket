package macoredroid;

import org.springframework.amqp.core.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebSocketServerApplication {
    @Bean
    Queue queue(){
        return new Queue("marketDepth");
    }

    @Bean
    Queue queue1(){
        return new Queue("finishedOrder");
    }

    @Bean
    Exchange exchange(){
        return new DirectExchange("marketDepth");
    }

    @Bean
    Exchange exchange1(){
        return new DirectExchange("finishedOrder");
    }



    @Bean
    Binding binding(){
        return BindingBuilder.bind(queue()).to(exchange()).with("marketDepth").noargs();
    }

    @Bean
    Binding binding1(){
        return BindingBuilder.bind(queue1()).to(exchange1()).with("finishedOrder").noargs();
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketServerApplication.class, args);
    }

}
