package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class SignUpController {
    private final UserService userService;


    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("signup")
    public String signupUser(@ModelAttribute User user, Model model, HttpSession session) {

        if (!userService.UsernameAvailable(user.getUsername())) {

            model.addAttribute("Error", "The username already registered.");
            return "signup";
        }

        int rows = userService.createUser(user);
        if (rows < 0) {
            model.addAttribute("Error",  "There was an error signing you up. Please try again.");
            return "signup";
        }

        session.setAttribute("Success", true);
        return "redirect:/login?success";

    }
}
