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
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String createUserPage() {
        return "signup";
    }

    @PostMapping()
    public String createNewUser(@ModelAttribute Users user, Model model) {
        String createUserError = null;

        if (!userService.isExistsUser(user.getUsername())) {
            createUserError = "The User Name already exists.";
        }

        if (StringUtils.isEmpty(createUserError)) {
            int idxUser = userService.createNewUser(user);
            if (idxUser < 0) {
                createUserError = "There was an error signing you up. Please try again.";
            }
        }

        if (createUserError == null) {
            model.addAttribute("createUserSuccess", true);
        } else {
            model.addAttribute("createUserError", createUserError);
        }

        return "signup";
    }
}