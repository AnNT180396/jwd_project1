package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsersMapper {
    @Select("SELECT * FROM USERS WHERE username = #{username}")
    Users getUserByName(String userName);

    @Select("SELECT * FROM USERS WHERE username = #{userId}")
    Users getUserById(String userId);


    @Insert("INSERT INTO USERS (username, salt, password, firstname, lastname) VALUES (#{username}, #{salt}, #{password}, #{firstName}, #{lastName}) ")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int createNewUser(Users user);
}