package com.document.documentservice.Services;

import com.document.documentservice.Domain.Enums.MessageAction;
import com.document.documentservice.Domain.Models.DocumentDTO;
import com.document.documentservice.Domain.Models.MessageWrapper;
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

    @Value("queueDataStatus.name")
    private String queueDataStatus;


    private final RabbitTemplate rabbitTemplate;
    private CountDownLatch latch;
    private String finalStatus;
    private final static Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    @Autowired
    public DocumentProcessingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * Method sends document model on postgres microservice and
     * wait result all microservices
     * @param file uploaded file
     * @return Successful if all action ended was success, else - error
     */
    public String uploadFile(MultipartFile file) {
        try {
            DocumentDTO documentDTO = new DocumentDTO(file.getOriginalFilename(), file.getContentType());
            MessageWrapper<DocumentDTO> message = new MessageWrapper<>(MessageAction.UPLOAD.toString(), documentDTO);

            System.out.println(documentDTO);
            System.out.println(message.getAction() + " " + message.getPayload().toString());

            latch = new CountDownLatch(1);  // set waiting one status

            sendInQueue(queueName, message);

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
     * Method delete all entity in db
     * @return status code
     */
    public String deleteAllDoc(){
        try{
            MessageWrapper<String> message = new MessageWrapper<>(MessageAction.DELETE.toString(), MessageAction.DELETE.toString());
            sendInQueue(queueDataStatus, message);
            return "Successful";
        } catch (Exception ex) {
            logger.error("Error with deleting all document: " + ex);
            return "Error";
        }
    }

    /**
     * Method for getting final status
     * This status shows result all microservices
     * @param statusData - final status
     */
    @RabbitListener(queues = "FinalStatusQueue")
    public void receiveStatus(MessageWrapper<String> statusData) {
        logger.info("Status was got: " + statusData);
        finalStatus = statusData.getPayload();
        latch.countDown();
    }

    /**
     * Method sends a message to the queue
     * @param object sent message or document
     */
    private void sendInQueue(String queueName,MessageWrapper<?> object) {
        rabbitTemplate.convertAndSend(queueName, object);
    }


    /**
     * Method checks file, if this document valid
     * @param file uploaded file
     * @return true, if this document is valid, else false
     */
    public boolean isValid(MultipartFile file) {
        return !file.isEmpty() && (isDocx(file) || isPdf(file));
    }

    /**
     * Method checks file, it pdf or not
     * @param file uploaded file
     * @return true if it pdf, else - false
     */
    private boolean isPdf(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && file.getContentType() != null &&
                filename.toLowerCase().endsWith(".pdf") && file.getContentType().equals("application/pdf");
    }

    /**
     * Method checks file, it docx or not
     * @param file uploaded file
     * @return true if it docx, else false
     */
    private boolean isDocx(MultipartFile file){
        String filename = file.getOriginalFilename();
        return filename != null && file.getContentType() != null &&
                filename.toLowerCase().endsWith(".docx") && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
