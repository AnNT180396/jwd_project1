package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/signup")
public class SignUpController {

    private final UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signUpPage() {
        return "signup";
    }

    @PostMapping()
    public String signUpUserPage(@ModelAttribute Users user, Model model) {
        String signUpError = null;

        if (!userService.isExistsUser(user.getUsername())) {
            signUpError = "The User Name already exists.";
        }

        if (StringUtils.isEmpty(signUpError)) {
            int idxUser = userService.createNewUser(user);
            if (idxUser < 0) {
                signUpError = "There was an error signing you up. Please try again.";
            }
        }

        if (signUpError == null) {
            model.addAttribute("signUpSuccess", true);
        } else {
            model.addAttribute("signUpError", signUpError);
        }

        return "signup";
    }
}