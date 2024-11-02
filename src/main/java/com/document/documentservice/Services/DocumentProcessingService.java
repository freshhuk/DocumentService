package com.document.documentservice.Services;

import com.document.documentservice.Domain.Models.DocumentDTO;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Setter
@Service
public class DocumentProcessingService {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private CountDownLatch latch;
    private String finalStatus;
    private final static Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    @Autowired
    public DocumentProcessingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String uploadFile(MultipartFile file) {
        try {
            DocumentDTO documentDTO = new DocumentDTO(file.getOriginalFilename(), file.getContentType());

            latch = new CountDownLatch(1);  // set waiting one status

            sendInQueue(documentDTO);

            boolean received = latch.await(5, TimeUnit.SECONDS); // wait 5 seconds until we get the status

            if (!received) {
                logger.error("Status not received in time");
                return "Error: Status not received in time";
            }


            return finalStatus.equals("AllDone") ? "Successful" : "Error";

        } catch (Exception ex) {
            logger.error("Error with file upload " + ex);
            return "Error";
        }
    }

    /**
     * Method for getting final status
     * This status shows result all microservices
     * @param statusData - final status
     */
    @RabbitListener(queues = "FinalStatusQueue")
    public void receiveStatus(String statusData) {
        logger.info("Status was got: " + statusData);
        finalStatus = statusData;
        latch.countDown();
    }

    private void sendInQueue(DocumentDTO documentDTO) {
        rabbitTemplate.convertAndSend(queueName, documentDTO);
    }

    public boolean isValid(MultipartFile file) {
        return !file.isEmpty() && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
