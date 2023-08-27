package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.FilesService;
import com.udacity.jwdnd.course1.cloudstorage.services.NotesService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class HomeController {
    Logger logger = Logger.getLogger(HomeController.class.getName());
    private final FilesService filesService;
    private final UserService usersService;
    private final NotesService notesService;
    private final CredentialsService credentialsService;
    private final EncryptionService encryptionService;

    public HomeController(FilesService filesService, UserService usersService, NotesService notesService,
                          CredentialsService credentialService, EncryptionService encryptionService) {
        this.filesService = filesService;
        this.usersService = usersService;
        this.notesService = notesService;
        this.credentialsService = credentialService;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        Users user = (Users) authentication.getPrincipal();
        logger.info("UserId " + user.getUserId());
        model.addAttribute("files", filesService.getAllFiles(user.getUserId()));
        logger.info("Size files " + String.valueOf(filesService.getAllFiles(user.getUserId()).size()));
        model.addAttribute("notes", notesService.getAllNotes(user.getUserId()));
        logger.info("Size notes " + String.valueOf(notesService.getAllNotes(user.getUserId()).size()));
        model.addAttribute("credentials", credentialsService.getAllCredentials(user.getUserId()));
        logger.info("Size credentials  " + String.valueOf(credentialsService.getAllCredentials(user.getUserId()).size()));
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }
}