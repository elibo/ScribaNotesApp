package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
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


@ContentView(R.layout.activity_edit_note)
public class EditNoteActivity extends RoboActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    public TextView valTv, valTv2;
    public Thread first, second;
    private String mode;
    @InjectView(R.id.note_title)
    private EditText noteTitleText;
    @InjectView(R.id.note_content)
    private EditText noteContentText;
    private Note note;
    private SpannableStringBuilder ssbContent;
    private SpannableStringBuilder ssbTitle;
    private ActionMode mActionMode = null;


    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    public static Intent buildIntent(Context context) {
        return buildIntent(context, null);
    }

    public static Note getExtraNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_NOTE);
    }

    @Override
    public void onActionModeStarted(final ActionMode mode) {
        if (mActionMode == null) {
            mActionMode = mode;
        }
        super.onActionModeStarted(mode);

    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        mActionMode = null;
        super.onActionModeFinished(mode);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stopThread1();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        valTv = (TextView) findViewById(R.id.valTv);
        valTv.setText(String.valueOf(0));
        valTv2 = (TextView) findViewById(R.id.valTv2);
        valTv2.setText(String.valueOf(0));
        mode = "";
        ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
        ssbContent = (SpannableStringBuilder) noteContentText.getText();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        if (note != null) {
            noteTitleText.setText(com.materialnotes.activity.Html.fromHtml(note.getTitle()));
            noteContentText.setText(com.materialnotes.activity.Html.fromHtml(note.getContent()));
        } else {
            note = new Note();
            note.setCreatedAt(new Date());
        }
        firstThread();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

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

    private boolean isNoteFormOk() {
        return !Strings.isNullOrBlank(noteTitleText.getText().toString()) &&
                !Strings.isNullOrBlank(noteContentText.getText().toString());
    }

    private void setNoteResult() {
        note.setTitle(com.materialnotes.activity.Html.toHtml(noteTitleText.getText()));
        note.setContent(com.materialnotes.activity.Html.toHtml(noteContentText.getText()));
        note.setUpdatedAt(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
    }

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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    /**
     * This is the start of the text modifications section
     **/

    public void clearFormat() {

        if (noteContentText.hasSelection())
            clearContent();
        else if (noteTitleText.hasSelection())
            clearTitle();
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
            if (ss[i].getStyle() == Typeface.BOLD_ITALIC || ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC) {
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
            if (ss[i].getStyle() == Typeface.BOLD_ITALIC || ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC) {
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

    public void boldItalicText() {
        tags(false);
        if (noteContentText.hasSelection()) {

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            ssbContent.setSpan(new UnderlineSpan(), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
            ssbContent.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

        } else if (noteTitleText.hasSelection()) {

            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbTitle.setSpan(new UnderlineSpan(), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
            ssbTitle.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

        }
    }

    public void highlightText() {
        tags(false);
        if (noteContentText.hasSelection()) {

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            ssbContent.setSpan(new BackgroundColorSpan(Color.YELLOW), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

        } else if (noteTitleText.hasSelection()) {

            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            ssbTitle.setSpan(new BackgroundColorSpan(Color.YELLOW), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
        }

    }

    public void deleteText() {
        tags(false);
        if (noteContentText.hasSelection()) {

            ssbContent = (SpannableStringBuilder) noteContentText.getText();
            int start = noteContentText.getSelectionStart();
            int end = noteContentText.getSelectionEnd();
            ssbContent.delete(start, end);

        } else if (noteTitleText.hasSelection()) {

            ssbTitle = (SpannableStringBuilder) noteTitleText.getText();
            int start = noteTitleText.getSelectionStart();
            int end = noteTitleText.getSelectionEnd();
            ssbTitle.delete(start, end);
        }

    }

    public void selectText() {
        tags(true);
        mode = "sl";
    }

    /**
     * End of the text modifications section
     **/

    public void tags(final boolean tag) {

        noteTitleText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }


            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }
            });

        noteContentText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }


            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        noteContentText.setLongClickable(tag);
        noteTitleText.setLongClickable(tag);
    }

    /**
     * This is the star of the threads section
     **/

    public void firstThread() {
        first = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                valTv.setText(String.valueOf(HRSActivity.mHrmValue));
                                stopThread1();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        first.start();
    }

    public void stopThread1() {
        if (HRSActivity.mHrmValue > 600 && HRSActivity.mHrmValue < 901) {
            Snackbar.make(getCurrentFocus(), "HIGHLIGHT", Snackbar.LENGTH_INDEFINITE).show();
            mode = "hl";
            //secondThread();
            //first.interrupt();
        } else if (HRSActivity.mHrmValue > 300 && HRSActivity.mHrmValue < 601) {
            Snackbar.make(getCurrentFocus(), "UNDERLINE", Snackbar.LENGTH_INDEFINITE).show();
            mode = "ul";
            //secondThread();
            //first.interrupt();
        } else if (HRSActivity.mHrmValue < 301 && HRSActivity.mHrmValue >50) {
            Snackbar.make(getCurrentFocus(), "DELETE", Snackbar.LENGTH_INDEFINITE).show();
            mode = "dl";
            //secondThread();
            //first.interrupt();
        } else if (HRSActivity.mHrmValue > 900) {
            Snackbar.make(getCurrentFocus(), "SELECT", Snackbar.LENGTH_INDEFINITE).show();
            mode = "sl";
            //secondThread();
            //first.interrupt();
        }
        secondThread();
        first.interrupt();
    }

    public void secondThread() {
        second = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                valTv2.setText(String.valueOf(HRSActivity.mHrmValue));
                                formatText();
                                stopThread2();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        second.start();
    }

    public void stopThread2() {
        if (Float.valueOf(valTv2.getText().toString()) < 50) {
            second.interrupt();
            firstThread();
        }
    }

    public void formatText() {
        if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("dl")) {
            deleteText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("ul")) {
            boldItalicText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("hl")) {
            highlightText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("sl")) {
            selectText();
        }
    }

    /**
     * End of the threads section
     **/

}

