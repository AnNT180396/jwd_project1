package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Notes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotesMapper {
    @Select("SELECT * FROM NOTES WHERE userid = #{userId} ")
    List<Notes> getNoteByUserId(Integer userId);

    @Insert("INSERT INTO NOTES (notetitle, notedescription, userid) VALUES (#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int addNewNote(Notes note);

    @Update("UPDATE NOTES SET notetitle = #{noteTitle}, notedescription = #{noteDescription} WHERE noteid = #{noteId}")
    void editNote(Notes note);

    @Delete("DELETE FROM NOTES WHERE noteid = #{noteId}")
    int deleteNoteById(int noteId);

    @Select("SELECT * FROM NOTES WHERE noteid = #{credentialId}")
    Notes getNoteById(Integer noteId);
}