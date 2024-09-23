package com.document.documentservcice.documentservice.Controllers;

import com.document.documentservcice.documentservice.Services.DocumentProcessingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class for end-point and working with other microservice
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class APIController {

    private final DocumentProcessingService documentService;

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
    //как вариант для регестрации скинуть новый микросревис под эту задачу
}
