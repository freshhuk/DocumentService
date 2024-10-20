package com.document.documentservice.Controllers;

import com.document.documentservice.Domain.Models.DocumentDTO;
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


    /**
     * This method accepts a file, if it is valid,
     * then this file is sent to the microservice
     * for loading into the database and subsequent processing
     * @param file  uploaded file from html form
     * @return status code
     */
    @PostMapping("/uploadDocument")
    public ResponseEntity<String> uploadDocument(MultipartFile file){

        if(documentService.isValid(file)){
            String result = documentService.uploadFile(file);
            return result.equals("Successful") ?
                    ResponseEntity.ok("File uploaded") :
                    ResponseEntity.badRequest().body("File not uploaded");
        }
        return ResponseEntity.badRequest().body("File is not valid");
    }
}
