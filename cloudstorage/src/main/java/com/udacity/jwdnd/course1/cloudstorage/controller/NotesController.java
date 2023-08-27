package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Notes;
import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import com.udacity.jwdnd.course1.cloudstorage.services.NotesService;
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
@RequestMapping("/home/notes")
public class NotesController {
    Logger logger = Logger.getLogger(NotesController.class.getName());
    private NotesService notesService;
    private UsersMapper usersMapper;

    public NotesController(NotesService notesService, UsersMapper usersMapper) {
        this.notesService = notesService;
        this.usersMapper = usersMapper;
    }

    @PostMapping
    public String handleUpsertNote(Authentication authentication, Notes note, RedirectAttributes redirectAttributes) {

        String createNoteError = null;
        try {
            Users user = (Users) authentication.getPrincipal();
            logger.info("handleUpsertNote" + String.valueOf(Objects.isNull(user)));
            if (Objects.isNull(user)) {
                createNoteError = "The User Login not found. Can't create credential";
                redirectAttributes.addFlashAttribute("error", createNoteError);
                return "redirect:/result?error";
            }

            if (note.getNoteId() != null) {
                notesService.editNote(note);
            } else {
                notesService.addNewNote(note, user.getUserId());
            }
        } catch (Exception e) {
            logger.info( e.getMessage());
            logger.info( "Can't create note");
            redirectAttributes.addFlashAttribute("error", "Can't create note");
            return "redirect:/result?error";
        }
        return "redirect:/result?success";

    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam("id") int noteId, Authentication authentication,
                             RedirectAttributes redirectAttributes){

        Users user = (Users) authentication.getPrincipal();
        if (Objects.isNull(user)) {
            redirectAttributes.addAttribute("error", "Unable to delete the note. Because User not found.");
            return "redirect:/result?error";
        }

        if(noteId > 0){
            notesService.deleteNoteById(noteId);
            return "redirect:/result?success";
        }

        redirectAttributes.addAttribute("error", "Unable to delete the note. Because User not found.");
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