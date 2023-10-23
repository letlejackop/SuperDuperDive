package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/credential")
public class CredentialController {
    private final CredentialService credentialService;
    private final UserService userService;

    public CredentialController(CredentialService credentialService, UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping()
    public String addCredential(Model model, Credential credential) {
        if (credential.getCredentialId() != null) {
            return "forward:/credential/update";
        }
        User currentUser = userService.getUser(
                SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            credentialService.addCredential(credential, currentUser.getUserId());
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }

    @GetMapping("/delete")
    public String deleteCredential(@RequestParam("credid") Integer credentialId) {
        Credential credential = credentialService.getCredentialById(credentialId);
        User currentUser = userService.getUser(
                SecurityContextHolder.getContext().getAuthentication().getName());

        if (!Objects.equals(currentUser.getUserId(), credential.getUserId())) {
            return "redirect:/result?success=false";
        }
        try {
            credentialService.deleteCredential(credentialId);
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }


    @PostMapping("/update")
    public String postUpdateCredential(Model model, Credential credential) {
        try {
            credentialService.updateCredential(credential);
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }
}
