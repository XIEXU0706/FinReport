package org.example.finreport.module.report.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportQueueConfig {

    public static final String EXCHANGE = "report.exchange";
    public static final String QUEUE = "report.generate.queue";
    public static final String ROUTING_KEY = "report.generate";

    @Bean
    public DirectExchange reportExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue reportGenerateQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding reportBinding(DirectExchange reportExchange, Queue reportGenerateQueue) {
        return BindingBuilder.bind(reportGenerateQueue)
                .to(reportExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();   // 将java对象转换为json字符串
    }
}
