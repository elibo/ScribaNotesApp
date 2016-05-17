package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;

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
    private SpannableStringBuilder ssbContent;
    private SpannableStringBuilder ssbTitle;
    TextView tv;
    private String mode;

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

        mode="";
        tv=(TextView)findViewById(R.id.value);
        ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
        ssbContent = (SpannableStringBuilder) noteContentText.getText();
        // Starts the components //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Shows the go back arrow
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // gets the note from the intent
        if (note != null) { // Edit existing note
            noteTitleText.setText(com.materialnotes.activity.Html.fromHtml(note.getTitle()));
            noteContentText.setText(com.materialnotes.activity.Html.fromHtml(note.getContent()));
        } else { // New note

            note = new Note();
            note.setCreatedAt(new Date());
        }


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(150);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(String.valueOf(HRSActivity.mHrmValue));
                                formatText();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
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
        note.setTitle(com.materialnotes.activity.Html.toHtml(noteTitleText.getText()));
        note.setContent(com.materialnotes.activity.Html.toHtml(noteContentText.getText()));
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
            Toast.makeText(getBaseContext(),
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

        if (!mode.equals("bold")){
        Snackbar.make(getCurrentFocus(),"BOLD MODE",Snackbar.LENGTH_INDEFINITE).show();
        mode= "bold";
        }

        if (noteTitleText.hasSelection()) {

            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbTitle.setSpan(new StyleSpan(Typeface.BOLD), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        } else if (noteContentText.hasSelection()){

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            ssbContent.setSpan(new StyleSpan(Typeface.BOLD), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        }
    }

    public void underlineFormat() {

       if (!mode.equals("underline")){
            Snackbar.make(getCurrentFocus(),"UNDERLINE MODE",Snackbar.LENGTH_INDEFINITE).show();
            mode= "underline";
        }
        if (noteTitleText.hasSelection()) {

            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbTitle.setSpan(new UnderlineSpan(), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        } else if (noteContentText.hasSelection()) {

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            ssbContent.setSpan(new UnderlineSpan(), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        }
    }

    public void highlightText(){
        if (!mode.equals("hl")){
            Snackbar.make(getCurrentFocus(),"HIGHLIGHT MODE",Snackbar.LENGTH_INDEFINITE).show();
            mode= "hl";
        }
        if (noteContentText.hasSelection()) {

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            ssbContent.setSpan(new BackgroundColorSpan(Color.YELLOW), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
        } else if (noteTitleText.hasSelection())
        {
            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbTitle.setSpan(new BackgroundColorSpan(Color.YELLOW), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
        }

    }

    public void clearFormat() {

        if (noteContentText.hasSelection())
            clearContent();
        else
            clearTitle();

    }

    public void formatText() {

        if (HRSActivity.mHrmValue > 900){
            if(!mode.equals("selection")){
                Snackbar.make(getCurrentFocus(),"SELECTION MODE",Snackbar.LENGTH_INDEFINITE).show();
                mode="selection";
            }

        } else if (HRSActivity.mHrmValue >= 600 && HRSActivity.mHrmValue < 900) {
            boldFormat();
        } else if (HRSActivity.mHrmValue < 600 && HRSActivity.mHrmValue >= 300) {
            underlineFormat();
        } else if (HRSActivity.mHrmValue >= 0 && HRSActivity.mHrmValue < 300){
            highlightText();
        }


    }

    public void clearTitle() {
        ssbTitle = (SpannableStringBuilder) noteTitleText.getText();

        StyleSpan[] ss = ssbTitle.getSpans(noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), StyleSpan.class);
        UnderlineSpan[] us = ssbTitle.getSpans(noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), UnderlineSpan.class);
        BackgroundColorSpan[] bgSpan = ssbTitle.getSpans(noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), BackgroundColorSpan.class);


        for (int i = 0; i < bgSpan.length; i++) {
            ssbTitle.removeSpan(bgSpan[i]);
        }

        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC || ss[i].getStyle() == Typeface.BOLD_ITALIC) {
                ssbTitle.removeSpan(ss[i]);
            }
        }

        for (int i = 0; i < us.length; i++) {
            ssbTitle.removeSpan(us[i]);
        }


        noteTitleText.setText(ssbTitle);
    }

    public void clearContent() {
        ssbContent = (SpannableStringBuilder) noteContentText.getText();

        StyleSpan[] ss = ssbContent.getSpans(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), StyleSpan.class);
        UnderlineSpan[] us = ssbContent.getSpans(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), UnderlineSpan.class);
        BackgroundColorSpan[] bgSpan = ssbContent.getSpans(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), BackgroundColorSpan.class);


        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC || ss[i].getStyle() == Typeface.BOLD_ITALIC) {
                ssbContent.removeSpan(ss[i]);
            }
        }

        for (int i = 0; i < us.length; i++) {
            ssbContent.removeSpan(us[i]);
        }

        for (int i = 0; i < bgSpan.length; i++) {
            ssbContent.removeSpan(bgSpan[i]);
        }

        noteContentText.setText(ssbContent);
    }


}

