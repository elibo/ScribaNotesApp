package com.materialnotes.data.dao;

import com.google.inject.ImplementedBy;
import com.materialnotes.data.Note;
import com.materialnotes.data.dao.impl.sqlite.NoteSQLiteDAO;

import java.util.List;


@ImplementedBy(NoteSQLiteDAO.class)
public interface NoteDAO {

    List<Note> fetchAll();

    void insert(Note note);

    void update(Note note);

    void delete(Note note);
}