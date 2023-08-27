package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.logging.Logger;

@Controller
@RequestMapping("/home/credentials")
public class CredentialsController {
    Logger logger = Logger.getLogger(CredentialsController.class.getName());

    private CredentialsService credentialsService;
    private UsersMapper usersMapper;

    public CredentialsController(CredentialsService credentialsService, UsersMapper userMapper) {
        this.credentialsService = credentialsService;
        this.usersMapper = usersMapper;
    }

    @PostMapping
    public String handleUpsertCredential(Authentication authentication, Credentials credentials,
                                         RedirectAttributes redirectAttributes){
        String createCredentialError = null;
        try {
            Users user = (Users) authentication.getPrincipal();

            if (Objects.isNull(user)) {
                createCredentialError = "The User Login not found. Can't create credential";
                redirectAttributes.addFlashAttribute("error", createCredentialError);
                return "redirect:/result?error";
            }
            logger.info("editCredential " + credentials.getCredentialId());
            if (credentials.getCredentialId() != null) {
                credentialsService.editCredential(credentials);
            } else {
                credentialsService.addNewCredential(credentials, user.getUserId());
            }
        } catch (Exception e) {
            logger.info( "Can't create credential");
            redirectAttributes.addFlashAttribute("error", "Can't create credential");
            return "redirect:/result?error";
        }
        return "redirect:/result?success";
    }

    @GetMapping("/delete")
    public String deleteCredential(@RequestParam("id") int credentialId, Authentication authentication,
                             RedirectAttributes redirectAttributes){

        Users user = (Users) authentication.getPrincipal();
        if (Objects.isNull(user)) {
            redirectAttributes.addAttribute("error", "Unable to delete the credentials. Because User not found.");
            return "redirect:/result?error";
        }

        if(credentialId > 0) {
            credentialsService.deleteCredentialById(credentialId);
            return "redirect:/result?success";
        }

        redirectAttributes.addAttribute("error", "Unable to delete the credentials. Because User not found.");
        return "redirect:/result?error";
    }

    private Integer getUser(String userName) {
        try {
            return usersMapper.getUserByName(userName).getUserId();
        } catch (Exception e) {
            logger.info( "Can't get User Information");
            return null;
        }
    }
}