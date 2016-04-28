package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;

import org.xml.sax.XMLReader;

import java.util.Date;

import no.nordicsemi.android.scriba.hrs.HRSActivity;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Edit notes activity
 **/
@ContentView(R.layout.activity_edit_note)
public class EditNoteActivity extends RoboActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

    @InjectView(R.id.note_title)
    private EditText noteTitleText;
    @InjectView(R.id.note_content)
    private EditText noteContentText;

    private Note note;
    private SpannableStringBuilder ssbtitle, ssbcontent;


    /**
     * Makes the intent to call the activity with an existing note
     *
     * @param context the context
     * @param note    the note to edit
     * @return the Intent.
     */
    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    /**
     * Makes the intent to call the activity for creating a note
     *
     * @param context the context that calls the activity
     * @return the Intent.
     */
    public static Intent buildIntent(Context context) {
        return buildIntent(context, null);
    }

    /**
     * Gets the edited note
     *
     * @param intent the intent from onActivityResult
     * @return the updated note
     */
    public static Note getExtraNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_NOTE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
        // Starts the components //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Shows the go back arrow
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // gets the note from the intent
        if (note != null) { // Edit existing note
            noteTitleText.setText(Html.fromHtml(note.getTitle()));
            noteContentText.setText(Html.fromHtml(note.getContent()));
        } else { // New note

            note = new Note();
            note.setCreatedAt(new Date());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.clear:
                clearFormat();
                return true;
            case R.id.action_save:
                if (isNoteFormOk()) {
                    setNoteResult();
                    finish();
                } else
                    validateNoteForm();
                return true;
            case R.id.format:
                formatText();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return {@code true} is the note has title and content; {@code false} every other case
     */
    private boolean isNoteFormOk() {
        return !Strings.isNullOrBlank(noteTitleText.getText().toString()) && !Strings.isNullOrBlank(noteContentText.getText().toString());
    }

    /**
     * Updates the note content with the layout texts and it makes the object as a result of the activity
     */
    private void setNoteResult() {
        note.setTitle(Html.toHtml(noteTitleText.getText()));
        note.setContent(Html.toHtml(noteContentText.getText()));
        note.setUpdatedAt(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
    }

    /**
     * Shows validating messages
     */
    private void validateNoteForm() {
        StringBuilder message = null;
        if (Strings.isNullOrBlank(noteTitleText.getText().toString())) {
            message = new StringBuilder().append(getString(R.string.title_required));
        }
        if (Strings.isNullOrBlank(noteContentText.getText().toString())) {
            if (message == null)
                message = new StringBuilder().append(getString(R.string.content_required));
            else message.append("\n").append(getString(R.string.content_required));
        }
        if (message != null) {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // Note not created or updated
        setResult(RESULT_CANCELED, new Intent());
        finish();

    }

    public void boldFormat() {

        if (noteTitleText.hasFocus()) {

            ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbtitle.setSpan(new StyleSpan(Typeface.BOLD), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        } else {

            ssbcontent = (SpannableStringBuilder) noteContentText.getText();
            ssbcontent.setSpan(new StyleSpan(Typeface.BOLD), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        }
    }

    public void italicFormat() {

        if (noteTitleText.hasFocus()) {

            ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbtitle.setSpan(new StyleSpan(Typeface.ITALIC), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        } else {

            ssbcontent = (SpannableStringBuilder) noteContentText.getText();
            ssbcontent.setSpan(new StyleSpan(Typeface.ITALIC), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        }
    }

    public void underlineFormat() {

        if (noteTitleText.hasFocus()) {

            ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbtitle.setSpan(new UnderlineSpan(), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        } else {

            ssbcontent = (SpannableStringBuilder) noteContentText.getText();
            ssbcontent.setSpan(new UnderlineSpan(), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        }
    }

    public void clearFormat() {

        if (noteContentText.hasFocus()){
            ssbcontent=(SpannableStringBuilder)noteContentText.getText();

            StyleSpan[] ss = ssbcontent.getSpans(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), StyleSpan.class);
            UnderlineSpan[] us = ssbcontent.getSpans(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), UnderlineSpan.class);


            for (int i = 0; i < ss.length; i++) {
                if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC || ss[i].getStyle() == Typeface.BOLD_ITALIC ) {
                    ssbcontent.removeSpan(ss[i]);
                }
            }

            for (int i = 0; i < us.length; i++) {
                ssbcontent.removeSpan(us[i]);
            }

            noteContentText.setText(ssbcontent);

        } else {
            ssbtitle=(SpannableStringBuilder)noteTitleText.getText();

            StyleSpan[] ss = ssbtitle.getSpans(noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), StyleSpan.class);
            UnderlineSpan[] us = ssbtitle.getSpans(noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), UnderlineSpan.class);


            for (int i = 0; i < ss.length; i++) {
                if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC || ss[i].getStyle() == Typeface.BOLD_ITALIC ) {
                    ssbtitle.removeSpan(ss[i]);
                }
            }

            for (int i = 0; i < us.length; i++) {
                ssbtitle.removeSpan(us[i]);
            }

            noteTitleText.setText(ssbtitle);

        }


    }

    public void formatText() {

        if (HRSActivity.mHrmValue > 600) {
            boldFormat();
        } else if (HRSActivity.mHrmValue > 300 && HRSActivity.mHrmValue < 600) {
            italicFormat();
        } else if (HRSActivity.mHrmValue < 300) {
            underlineFormat();
        }

    }

}