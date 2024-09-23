package com.document.documentservcice.documentservice.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentProcessingService {


    //сервис для валидации документа а так же отправка его в базу данных

    /**
     * Method checks is valid file
     * If this .docx file and file not empty then file is valid. Another - not valid
     * @param file - checking file
     * @return true if file is valid, else false
     */
    public boolean isValid(MultipartFile file){/
        return !file.isEmpty() && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    }
    public String uploadFile(MultipartFile file){
        return "ok";
    }
}
