package com.document.documentservice.Controllers;

import com.document.documentservice.Services.DocumentProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class APIControllerTest {

    @InjectMocks
    private APIController controller;
    @Mock
    private  DocumentProcessingService documentService;


    @Test
    void uploadDocument(){


       // var result = controller.uploadDocument();
    }

}
