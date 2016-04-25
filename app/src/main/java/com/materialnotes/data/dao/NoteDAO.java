package com.materialnotes.data.dao;

import com.google.inject.ImplementedBy;

import com.materialnotes.data.Note;
import com.materialnotes.data.dao.impl.sqlite.NoteSQLiteDAO;

import java.util.List;


@ImplementedBy(NoteSQLiteDAO.class)
public interface NoteDAO {

    /** @return all notes from the database*/
    List<Note> fetchAll();

    /**
     * Insert a note in the database
     *
     * @param note the note to insert
     */
    void insert(Note note);

    /**
     * Updates a note in the database
     *
     * @param note the note to update
     */
    void update(Note note);

    /**
     * Deletes a note from the database
     *
     * @param note the note to delete
     */
    void delete(Note note);
}