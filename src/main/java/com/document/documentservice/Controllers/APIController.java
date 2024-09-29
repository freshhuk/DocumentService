package com.document.documentservice.Controllers;

import com.document.documentservice.Services.DocumentProcessingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class for end-point and working with other microservice
 */
@RestController
@RequestMapping("/api")
public class APIController {

    private final DocumentProcessingService documentService;

    @Autowired
    public APIController(DocumentProcessingService documentService){
        this.documentService = documentService;
    }


    //Задачи о этом файле находятся в доках ворда
    @PostMapping("/uploadDocument")
    public ResponseEntity<String> uploadDocument(MultipartFile file){

        //Debug
        System.out.println(" " + file.getOriginalFilename() + " " + file.getContentType() + " |||");


        if(documentService.isValid(file)){
            String result = documentService.uploadFile(file);
            return result.equals("Successful") ?
                    ResponseEntity.ok("File uploaded") :
                    ResponseEntity.badRequest().body("File not uploaded");
        }
        return ResponseEntity.badRequest().body("File is not valid");
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody String message){
        if(!message.isEmpty()){
            documentService.send(message);
            return ResponseEntity.ok("Send message ok");
        }
        return  ResponseEntity.badRequest().body("Error");
    }
    //как вариант для регестрации скинуть новый микросревис под эту задачу
}
