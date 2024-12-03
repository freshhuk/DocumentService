package com.document.documentservice.Controllers;

import com.document.documentservice.Domain.Models.LoginModel;
import com.document.documentservice.Domain.Models.RegisterModel;
import com.document.documentservice.Security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/reg")
    public ResponseEntity<String>  register(@ModelAttribute RegisterModel registerModel){
        if(registerModel != null){

            System.out.println(registerModel);

            String result = authService.registration(registerModel);
            return result.equals("Successful") ?
                    ResponseEntity.ok("User register") :
                    ResponseEntity.badRequest().body("Something was wrong");
        }
        return ResponseEntity.badRequest().body("Error model is null");
    }
    @PostMapping("/log")
    public ResponseEntity<String> login(@ModelAttribute  LoginModel loginModel){
        if(loginModel != null){
            String result = authService.login(loginModel);
            return result.equals("Successful") ?
                    ResponseEntity.ok("User register") :
                    ResponseEntity.badRequest().body("Something was wrong");
        }
        return ResponseEntity.badRequest().body("Error model is null");
    }
}
