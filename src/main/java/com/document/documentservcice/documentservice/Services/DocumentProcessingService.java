package com.document.documentservcice.documentservice.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentProcessingService {
    //сервис для валидации документа а так же отправка его в базу данных

    public boolean isValid(MultipartFile file){
        return true;
    }
    public String uploadFile(MultipartFile file){
        return "ok";
    }
}
