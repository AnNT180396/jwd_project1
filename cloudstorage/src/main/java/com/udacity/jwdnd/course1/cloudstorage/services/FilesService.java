package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NotesMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import com.udacity.jwdnd.course1.cloudstorage.model.Notes;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FilesService {
    private final FilesMapper filesMapper;

    public FilesService(FilesMapper filesMapper) {
        this.filesMapper = filesMapper;
    }

    public void addNewFile(MultipartFile fileUpload, int userId) throws IOException {
        Files file = new Files();
        try {
            file.setContentType(fileUpload.getContentType());
            file.setFileData(fileUpload.getBytes());
            file.setFileName(fileUpload.getOriginalFilename());
            file.setFileSize(Long.toString(fileUpload.getSize()));
            file.setUserId(userId);
        } catch (IOException e) {
            throw e;
        }
        int idxFile = filesMapper.createFile(file);
    }

    public List<Files> getAllFiles(Integer userid){
        return filesMapper.getAllFiles(userid);
    }

    public boolean isAvailableFile(String fileName, Integer userId) {
        Files files = filesMapper.getFilesByUserAndFileName(userId, fileName);

        if(files != null) {
            return false;
        }

        return true;
    }

    public Integer deleteFileById(Integer fileId) {
        return filesMapper.deleteFileById(fileId);
    }

    public Files getFileById(Integer fileId){
        return filesMapper.getFilesById(fileId);
    }
}