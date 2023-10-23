package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service()
public class CredentialService {
    private final EncryptionService encryptionService;
    private final CredentialMapper credentialMapper;

    public CredentialService(EncryptionService encryptionService, CredentialMapper credentialMapper) {
        this.encryptionService = encryptionService;
        this.credentialMapper = credentialMapper;
    }

    public void addCredential(Credential credential, int userId) {
        credential.setKey(generateKey());
        credential.setPassword(encryptionService.encryptValue(credential.getPassword(), credential.getKey()));
        credential.setUserId(userId);
        credentialMapper.insertCredentials(credential);
    }

    private String generateKey() {
        final String CHARACTER_SET = "faisal123";

        int keyLength = 16;

        SecureRandom random = new SecureRandom();

        StringBuilder builder = new StringBuilder(keyLength);
        for (int i = 0; i < keyLength; i++) {
            int offset = random.nextInt(CHARACTER_SET.length());
            builder.append(CHARACTER_SET.charAt(offset));
        }
        return builder.toString();
    }

    public void updateCredential(Credential newCredential) {
        newCredential.setKey(generateKey());
        newCredential.setPassword(encryptionService.encryptValue(newCredential.getPassword(), newCredential.getKey()));
        credentialMapper.updateCredential(newCredential);
    }

    public void deleteCredential(Integer credentialId) {
        credentialMapper.deleteCredential(credentialId);
    }

    public List<Credential> getCredentialsList(Integer userId) {
        List<Credential> credList = credentialMapper.getAllCredentials(userId);
        for (Credential cred : credList) {
            cred.setPassword(encryptionService.decryptValue(cred.getPassword(), cred.getKey()));
        }

        return credList;
    }

    public Credential getCredentialById(Integer credentialId) {
        return credentialMapper.getCredential(credentialId);
    }
}
