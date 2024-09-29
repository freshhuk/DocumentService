package com.document.documentservice.Services;

import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Service
public class DocumentProcessingService {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;

    public DocumentProcessingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    //сервис для валидации документа а так же отправка его в базу данных

    /**
     * Method checks is valid file
     * If this .docx file and file not empty then file is valid. Another - not valid
     * @param file - checking file
     * @return true if file is valid, else false
     */
    public boolean isValid(MultipartFile file){
        return !file.isEmpty() && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    }
    public String uploadFile(MultipartFile file){
        return "ok";
    }

    public void send(String message){
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
