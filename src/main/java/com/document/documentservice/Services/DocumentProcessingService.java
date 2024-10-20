package com.document.documentservice.Services;

import com.document.documentservice.Domain.Models.DocumentDTO;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CountDownLatch;

@Setter
@Service
public class DocumentProcessingService {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private CountDownLatch latch = new CountDownLatch(1);
    private String status="";

    public DocumentProcessingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public String uploadFile(MultipartFile file){
        try{
            DocumentDTO documentDTO = new DocumentDTO(file.getOriginalFilename(), file.getContentType());
            sendInQueue(documentDTO);
            latch.await();
            System.out.println("Hmm" + status);
            if(status.equals("Successful")){
                return "Successful";
            } else{
                return "Error";
            }

        } catch (Exception ex){
            System.out.println("Error from uploaded file " + ex);
            return "Error";
        }
    }

    @RabbitListener(queues = "StatusDataQueue")
    public void receiveStatus(String statusData){
        System.out.println("Status" + statusData);
        if(statusData.equals("AllDone")){
            status = "Successful";
        } else if (statusData.equals("AllError")){
            status = "Error";
        }
    }

    private void sendInQueue(DocumentDTO documentDTO){
        rabbitTemplate.convertAndSend(queueName, documentDTO);
    }
    /**
     * Method checks is valid file
     * If this .docx file and file not empty then file is valid. Another - not valid
     * @param file  checking file
     * @return true if file is valid, else false
     */
    public boolean isValid(MultipartFile file){
        return !file.isEmpty() && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    }
}
