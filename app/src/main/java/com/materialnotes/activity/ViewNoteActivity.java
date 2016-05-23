package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.view.ShowHideOnScroll;
import com.shamanland.fab.FloatingActionButton;

import java.text.DateFormat;

import no.nordicsemi.android.scriba.hrs.HRSActivity;
import no.nordicsemi.android.scriba.profile.BleProfileActivity;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_view_note)
public class ViewNoteActivity extends RoboActionBarActivity {

    private static final int EDIT_NOTE_RESULT_CODE = 8;
    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final String EXTRA_UPDATED_NOTE = "EXTRA_UPDATED_NOTE";
    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    @InjectView(R.id.scroll_view)
    private ScrollView scrollView;
    @InjectView(R.id.edit_note_button)
    private FloatingActionButton editNoteButton;
    @InjectView(R.id.note_title)
    private TextView noteTitleText;
    @InjectView(R.id.note_content)
    private TextView noteContentText;
    @InjectView(R.id.note_created_at_date)
    private TextView noteCreatedAtDateText;
    @InjectView(R.id.note_updated_at_date)
    private TextView noteUpdatedAtDateText;

    private int buttonColor;

    private Note note;


    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, ViewNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    public static Note getExtraUpdatedNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_UPDATED_NOTE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonColor = Color.parseColor("#00cc66");
        editNoteButton.setColor(buttonColor);
        editNoteButton.initBackground();
        // Starts the components //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Shows the arrow to go back
        scrollView.setOnTouchListener(new ShowHideOnScroll(editNoteButton, getSupportActionBar())); // Hides or shows the FAB and the Action Bar
        editNoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Go to edit note activity
                startActivityForResult(EditNoteActivity.buildIntent(ViewNoteActivity.this, note), EDIT_NOTE_RESULT_CODE);
            }
        });
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // Gets the note from the intent
        // Shows the note info in the layout
        noteTitleText.setText(com.materialnotes.activity.Html.fromHtml(note.getTitle()));
        noteContentText.setText(com.materialnotes.activity.Html.fromHtml(note.getContent()));
        noteCreatedAtDateText.setText(DATETIME_FORMAT.format(note.getCreatedAt()));
        noteUpdatedAtDateText.setText(DATETIME_FORMAT.format(note.getUpdatedAt()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed(); // Closes the activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                //The note was successfully edited and the activity finish with a result
                Intent resultIntent = new Intent();
                Note note = EditNoteActivity.getExtraNote(data);
                resultIntent.putExtra(EXTRA_UPDATED_NOTE, note);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) onBackPressed();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        // The note wasn't edited
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}