package com.materialnotes.widget;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;

import java.text.DateFormat;
import java.util.List;

/**
 * Notes adapter. Acts between the view and the data
 */
public class NotesAdapter extends BaseAdapter {

    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private final List<NoteViewWrapper> data;

    /**
     * Constructor.
     *
     * @param data the notes list we use as data source for this adaptor.
     */
    public NotesAdapter(List<NoteViewWrapper> data) {
        this.data = data;
    }

    /**
     * @return gets the number of notes in the notes list
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * @param position the position of the wanted note
     * @return the note in the wanted position
     */
    @Override
    public NoteViewWrapper getItem(int position) {
        return data.get(position);
    }

    /**
     * @param position the position
     * @return the same position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Shows the data of the chosen note
     *
     * @param position    the actual note position
     * @param convertView the visual component to use
     * @param parent      the visual component parent
     * @return the data view
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // inflate the visual component
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        // Starts the view with the note data
        NoteViewWrapper noteViewWrapper = data.get(position);
        holder.noteIdText.setText(String.valueOf(noteViewWrapper.note.getId()));
        holder.noteTitleText.setText(Html.fromHtml(noteViewWrapper.note.getTitle()));
        // cuts the string to 80 characters and appends "..."
        holder.noteContentText.setText(Html.fromHtml(noteViewWrapper.note.getContent().length() < 80 ? noteViewWrapper.note.getContent() : noteViewWrapper.note.getContent().substring(0, 80).concat("...")));
        holder.noteDateText.setText(DATETIME_FORMAT.format(noteViewWrapper.note.getUpdatedAt()));
        // Change the background if you select it
        if (noteViewWrapper.isSelected)
            holder.parent.setBackgroundColor(parent.getContext().getResources().getColor(R.color.selected_note));
            // if not it goes back to being transparent
        else
            holder.parent.setBackgroundColor(parent.getContext().getResources().getColor(android.R.color.transparent));
        return convertView;
    }

    /**
     * Wrapper for notes . Changes the background of the selected items.
     */
    public static class NoteViewWrapper {

        private final Note note;
        private boolean isSelected;

        /**
         * Creates a new NoteWrapper with the given note
         *
         * @param note the note.
         */
        public NoteViewWrapper(Note note) {
            this.note = note;
        }

        public Note getNote() {
            return note;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

    private static class ViewHolder {

        private TextView noteIdText;
        private TextView noteTitleText;
        private TextView noteContentText;
        private TextView noteDateText;

        private View parent;

        /**
         * Constructor. Find the visual components in the parent.
         *
         * @param parent a visual component.
         */
        private ViewHolder(View parent) {
            this.parent = parent;
            noteIdText = (TextView) parent.findViewById(R.id.note_id);
            noteTitleText = (TextView) parent.findViewById(R.id.note_title);
            noteContentText = (TextView) parent.findViewById(R.id.note_content);
            noteDateText = (TextView) parent.findViewById(R.id.note_date);
        }
    }
}