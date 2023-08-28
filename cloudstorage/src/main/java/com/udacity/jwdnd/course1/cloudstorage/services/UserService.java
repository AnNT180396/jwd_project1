package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    private final UsersMapper userMapper;
    private final HashService hashService;

    private PasswordEncoder passwordEncoder;

    public UserService(UsersMapper userMapper, HashService hashService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.hashService = hashService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isExistsUser(String userName) {
        return Objects.isNull(userMapper.getUserByName(userName));
    }

    public int createNewUser(Users user) {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//        String encodedSalt = Base64.getEncoder().encodeToString(salt);
//        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);
//        user.setSalt(encodedSalt);
//        user.setPassword(hashedPassword);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRole("USER");
        return userMapper.createNewUser(user);
    }

    public Users getUserByName(String userName) {
        return userMapper.getUserByName(userName);
    }

    public Users getUserById(String userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userMapper.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("");
        }
        return (UserDetails) user;
    }
}