package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }


    public void addNote(Note note, int userId) {
        note.setUserId(userId);
        noteMapper.insertNote(note);
    }

    public void updateNote(Note newNote) {
        noteMapper.updateNote(newNote);
    }

    public void deleteNote(Integer noteId) {
        noteMapper.deleteNote(noteId);
    }

    public List<Note> getNoteList(Integer userId) {
        return noteMapper.findAllNotes(userId);
    }

    public Note getNoteById(Integer noteId) {
        return noteMapper.findNote(noteId);
    }

}
