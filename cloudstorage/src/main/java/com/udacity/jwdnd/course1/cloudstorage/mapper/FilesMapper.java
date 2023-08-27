package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FilesMapper {
    @Select("SELECT * FROM FILES WHERE filename = #{fileName}")
    Files getFilesByName(String fileName);

    @Select("SELECT * FROM FILES WHERE fileid = #{fileId}")
    Files getFilesById(Integer fileId);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) " +
            "VALUES (#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int createFile(Files file);

    @Select("SELECT * FROM FILES WHERE userid = #{userid} ")
    List<Files> getAllFiles(Integer userId);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId}")
    int deleteFileById(int fileId);

    @Select("SELECT * FROM FILES WHERE userid = #{userId} AND filename = #{fileName}")
    Files getFilesByUserAndFileName(Integer userId, String fileName);
}