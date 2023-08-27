package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.controller.FilesController;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NotesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Notes;
import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Service
public class NotesService {
    Logger logger = Logger.getLogger(NotesService.class.getName());
    private NotesMapper notesMapper;

    public NotesService(NotesMapper notesMapper) {
        this.notesMapper = notesMapper;
    }

    public List<Notes> getAllNotes(Integer userId){
        List<Notes> notes = notesMapper.getNoteByUserId(userId);
        return notes;
    }

    public void addNewNote(Notes note, Integer userId){
        note.setUserId(userId);
        int idxNote = notesMapper.addNewNote(note);
    }

    public void editNote(Notes note) {
        notesMapper.editNote(note);
    }

    public void deleteNoteById(int noteId){
        notesMapper.deleteNoteById(noteId);
    }
}