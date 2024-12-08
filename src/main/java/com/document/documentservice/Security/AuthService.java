package com.document.documentservice.Security;

import com.document.documentservice.Domain.Enums.MessageAction;
import com.document.documentservice.Domain.Enums.QueueStatus;
import com.document.documentservice.Domain.Models.LoginModel;
import com.document.documentservice.Domain.Models.MessageWrapper;
import com.document.documentservice.Domain.Models.RegisterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Value("${queueAuthModel.name}")
    private String queueAuthModel;
    private final JWTService jwtService;
    private final RabbitTemplate rabbitTemplate;
    private CountDownLatch latch;
    private String authStatus;
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(JWTService jwtService,RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.jwtService =jwtService;
    }

    /**
     * Method for sends model in queue
     * @param registerModel model
     */
    public String registration(RegisterModel registerModel){
        try{
            MessageWrapper<RegisterModel> message = new MessageWrapper<>(MessageAction.REGISTER.toString(), registerModel);


            latch = new CountDownLatch(1);

            sendInQueue(queueAuthModel, message);

            boolean received = latch.await(5, TimeUnit.SECONDS); // wait 5 seconds until we get the status

            String username = jwtService.parseToken(authStatus).getSubject();
            if (username != null && !authStatus.equals(QueueStatus.BAD.toString())) {
                // Если токен валиден, авторизуем пользователя
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            if (!received) {
                logger.error("Status not received in time");
                return "Error: Status not received in time";
            }
            return !authStatus.equals(QueueStatus.BAD.toString()) ? authStatus : "Error";

        } catch (Exception ex){
            logger.error("Error with authorize method " + ex);
            return "Error";
        }
    }

    public String login(LoginModel loginModel){
        try{
            MessageWrapper<LoginModel> message = new MessageWrapper<>(MessageAction.LOGIN.toString(), loginModel);

            latch = new CountDownLatch(1);

            sendInQueue(queueAuthModel, message);

            boolean received = latch.await(5, TimeUnit.SECONDS); // wait 5 seconds until we get the status

            String username = jwtService.parseToken(authStatus).getSubject();
            if (username != null && !authStatus.equals(QueueStatus.BAD.toString())) {
                // Если токен валиден, авторизуем пользователя
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            if (!received) {
                logger.error("Status not received in time");
                return "Error: Status not received in time";
            }
            return !authStatus.equals(QueueStatus.BAD.toString()) ? authStatus : "Error";

        } catch (Exception ex){
            logger.error("Error with authorize method " + ex);
            return "Error";
        }
    }

    /**
     * Method for getting auth status
     * This status shows result authorization
     * @param statusData - auth status
     */
    @RabbitListener(queues = "queueAuthStatus")
    public void receiveStatus(MessageWrapper<String> statusData) {
        logger.info("Status was got: " + statusData);
        authStatus = statusData.getPayload();
        latch.countDown();
    }
    /**
     * Method sends a message to the queue
     * @param object sent message or document
     */
    private void sendInQueue(String queueName, MessageWrapper<?> object) {
        rabbitTemplate.convertAndSend(queueName, object);
    }
}
