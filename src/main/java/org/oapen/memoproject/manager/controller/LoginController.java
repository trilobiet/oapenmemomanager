package org.oapen.memoproject.manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoginController {

    @GetMapping(value = "/login")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String login(Model model, String error) {
    	
        if (error != null)
            model.addAttribute("error");
        
        return "loginform"; // src/main/resources/templates/loginform.html (thymeleaf)
    }
    
    @GetMapping(value = "/logout")
    public String logout(Model model) {

        model.addAttribute("msglogout");
        
        return "loginform"; // src/main/resources/templates/login.html (thymeleaf)
    }
}