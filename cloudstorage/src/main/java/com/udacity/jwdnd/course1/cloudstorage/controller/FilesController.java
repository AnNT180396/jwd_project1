package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import com.udacity.jwdnd.course1.cloudstorage.services.FilesService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@Controller
@RequestMapping("/home/files")
public class FilesController {
    Logger logger = Logger.getLogger(FilesController.class.getName());

    private FilesService filesService;
    private UsersMapper usersMapper;

    public FilesController(FilesService filesService, UsersMapper usersMapper) {
        this.filesService = filesService;
        this.usersMapper = usersMapper;
    }


    @PostMapping
    public String addNewFile(@RequestParam("fileUpload") MultipartFile file, Authentication authentication,
                                   RedirectAttributes redirectAttributes) throws IOException {
        String uploadError = null;

        Users user = (Users) authentication.getPrincipal();

        if (file.isEmpty()) {
            uploadError = "Please selected file non-empty.";
        }

        if (!filesService.isAvailableFile(file.getOriginalFilename(), user.getUserId())) {
            uploadError = "The file already exists in Database";
        }

        if(uploadError!=null) {
            redirectAttributes.addFlashAttribute("error", uploadError);
            return "redirect:/result?error";
        }

        filesService.addNewFile(file, user.getUserId());
        return "redirect:/result?success";
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam("id") Integer fileId, Authentication authentication, RedirectAttributes redirectAttributes){
        Users user = (Users) authentication.getPrincipal();
        if (Objects.isNull(user)) {
            redirectAttributes.addAttribute("error", "Unable to delete the file. Because User not found.");
            return "redirect:/result?error";
        }

        if(fileId > 0){
            filesService.deleteFileById(fileId);
            return "redirect:/result?success";
        }

        redirectAttributes.addAttribute("error", "Unable to delete the file. Because not find the file in database");
        return "redirect:/result?error";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer fileId){
        Files file = filesService.getFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\""+ file.getFileName()+"\"")
                .body(new ByteArrayResource(file.getFileData()));
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