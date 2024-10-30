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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Setter
@Service
public class DocumentProcessingService {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private CountDownLatch latch;
    private List<String> statusList = new ArrayList<>(); // Contains all status
    private final static Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    @Autowired
    public DocumentProcessingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String uploadFile(MultipartFile file) {
        try {
            DocumentDTO documentDTO = new DocumentDTO(file.getOriginalFilename(), file.getContentType());
            latch = new CountDownLatch(2); // wait status processing
            sendInQueue(documentDTO);

            latch.await(); // wait get status
            logger.info("All status: " + statusList);

            if (statusList.contains("AllDone")) {
                return "Successful";
            } else {
                return "Error";
            }

        } catch (Exception ex) {
            logger.error("Error with file: " + ex);
            return "Error";
        }
    }

    @RabbitListener(queues = "StatusDataQueue")
    public void receiveStatus(String statusData) {
        logger.info("Получен статус: " + statusData);
        statusList.add(statusData);

        latch.countDown();

    }

    private void sendInQueue(DocumentDTO documentDTO) {
        rabbitTemplate.convertAndSend(queueName, documentDTO);
    }

    /**
     * Проверяет, является ли файл допустимым (.docx и не пустой).
     * @param file проверяемый файл
     * @return true, если файл допустим, иначе false
     */
    public boolean isValid(MultipartFile file) {
        return !file.isEmpty() && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
