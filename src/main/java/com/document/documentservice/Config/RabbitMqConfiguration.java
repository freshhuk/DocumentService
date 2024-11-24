package com.document.documentservice.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Value("${queueFinalStatus.name}")
    private String queueFinalStatus;
    @Value("${queueMongoModel.name}")
    private String queueMongoModel;
    @Value("${queueAPIStatus.name}")
    private String queueAPIStatus;





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
    public Queue queueMongoModel(){
        return new Queue(queueMongoModel, false);
    }
    @Bean
    public Queue queueDataStatus(){
        return new Queue(queueDataStatus, false);
    }
    @Bean
    public Queue queueFinalStatus(){
        return new Queue(queueFinalStatus, false);
    }
    @Bean
    public Queue queueAPIStatus(){
        return new Queue(queueAPIStatus, false);
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
