package com.document.documentservice.Controllers;

import com.document.documentservice.Domain.Models.LoginModel;
import com.document.documentservice.Domain.Models.RegisterModel;
import com.document.documentservice.Security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            return result.equals("Error") ?
                    ResponseEntity.badRequest().body("Something was wrong") :
                    ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body("Error model is null");
    }
    @PostMapping("/log")
    public ResponseEntity<String> login(@ModelAttribute  LoginModel loginModel){
        if(loginModel != null){
            System.out.println(loginModel);

            String result = authService.login(loginModel);
            return result.equals("Error") ?
                    ResponseEntity.badRequest().body("Something was wrong") :
                    ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body("Error model is null");
    }
    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok("Пользователь аутентифицирован");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не аутентифицирован");
        }
    }

}
