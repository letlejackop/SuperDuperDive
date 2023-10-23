package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/note")
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }


    @PostMapping
    public String postNote(Model model, Note note) {
        try {
            if (note.getNoteId() != null) {
                return "forward:/note/update";
            }
            User currentUser = userService.getUser(
                    SecurityContextHolder.getContext().getAuthentication().getName());

            noteService.addNote(note, currentUser.getUserId());
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }
    @PostMapping("update")
    public String postUpdateNote(Model model, Note note) {
        try {
            noteService.updateNote(note);
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }
    @GetMapping("/delete")
    public String getDeleteNote(@RequestParam("noteId") Integer noteId) {
        try {
            Note note = noteService.getNoteById(noteId);
            User currentUser = userService.getUser(
                    SecurityContextHolder.getContext().getAuthentication().getName());

            if (!Objects.equals(currentUser.getUserId(), note.getUserId())) {
                return "redirect:/result?success=false";
            }
            noteService.deleteNote(noteId);
            return "redirect:/result?success=true";
        } catch (Error e) {
            return "redirect:/result?success=false";
        }
    }
}
