package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialsMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service("CredentialsService")
public class CredentialsService {
    private CredentialsMapper credentialsMapper;
    private EncryptionService encryptionService;

    public CredentialsService(CredentialsMapper credentialsMapper, EncryptionService encryptionService) {
        this.credentialsMapper = credentialsMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credentials> getAllCredentials(int userId){
        return credentialsMapper.getCredentialsByUserId(userId);
    }

    public void addNewCredential(Credentials credentials, Integer userId){
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credentials.getPassword(), encodedKey);
        credentials.setKey(encodedKey);
        credentials.setPassword(encryptedPassword);
        credentials.setUserId(userId);
        credentialsMapper.addNewCredentials(credentials);
    }

    public void editCredential(Credentials credentials){
        Credentials storedCredential = credentialsMapper.getCredentialById(credentials.getCredentialId());
        credentials.setKey(storedCredential.getKey());
        String encryptedPassword = encryptionService.encryptValue(credentials.getPassword(), credentials.getKey());
        credentials.setPassword(encryptedPassword);
        credentialsMapper.editCredential(credentials);
    }

    public int deleteCredentialById(int credentialId){
        return credentialsMapper.deleteCredentialById(credentialId);
    }

}