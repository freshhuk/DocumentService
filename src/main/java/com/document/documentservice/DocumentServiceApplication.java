package com.document.documentservice;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DocumentServiceApplication {

    private final RabbitAdmin rabbitAdmin;

    private final List<Queue> queues;

    @Autowired
    public DocumentServiceApplication(RabbitAdmin rabbitAdmin, List<Queue> queues) {
        this.rabbitAdmin = rabbitAdmin;
        this.queues = queues;
    }

    @PostConstruct
    public void declareQueues() {
        queues.forEach(rabbitAdmin::declareQueue);
    }

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }

}
