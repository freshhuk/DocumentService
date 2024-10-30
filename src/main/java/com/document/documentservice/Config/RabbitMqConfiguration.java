package com.document.documentservice.Config;

import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
public class RabbitMqConfiguration {

    @Value("${queue.name}")
    private String queueDataName;

    @Value("${queueMongo.name}")
    private String queueMongoName;

    @Value("${queueMongoStatus.name}")
    private String queueMongoStatus;

    @Value("${queueDataStatus.name}")
    private String queueDataStatus;

    @Value("${spring.rabbitmq.username}")
    private String queueUserName;

    @Value("${spring.rabbitmq.password}")
    private String queuePassword;
    @Bean
    public Queue queue(){
        return new Queue(queueDataName, false);
    }
    @Bean
    public Queue queueMongo(){
        return new Queue(queueMongoName, false);
    }
    @Bean
    public Queue queueMongoStatus(){
        return new Queue(queueMongoStatus, false);
    }
    @Bean
    public Queue queueDataStatus(){
        return new Queue(queueDataStatus, false);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("statusExchange");
    }

    @Bean
    public Binding bindingDone() {
        return BindingBuilder.bind(queueDataStatus()).to(directExchange()).with("doneRoutingKey");
    }
    @Bean
    public Binding bindingAllDone() {
        return BindingBuilder.bind(queueDataStatus()).to(directExchange()).with("allDoneRoutingKey");
    }
    @Bean
    public CachingConnectionFactory connectionFactory(){
        CachingConnectionFactory connection = new CachingConnectionFactory("localhost");
        connection.setUsername(queueUserName);
        connection.setPassword(queuePassword);
        return connection;
    }
    @Bean
    public RabbitAdmin rabbitAdmin(){
        return  new RabbitAdmin(connectionFactory());
    }
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
