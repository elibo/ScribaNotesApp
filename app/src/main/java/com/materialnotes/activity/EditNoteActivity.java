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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
    @InjectView(R.id.leftVal)
    private TextView firstThreadVal;
    @InjectView(R.id.rightVal)
    private TextView secondThreadVal;
    private Thread first, second;
    private int cont;
    private View.OnTouchListener listener;
    private String mode;
    @InjectView(R.id.note_title)
    private EditText noteTitleText;
    @InjectView(R.id.note_content)
    private EditText noteContentText;
    private Note note;
    private SpannableStringBuilder ssbContent;
    private SpannableStringBuilder ssbTitle;
    private ActionMode mActionMode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = "";
        cont = 0;
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

        listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (noteTitleText.hasSelection()) {
                    cont++;
                    if (cont == 1)
                        stopThread1();
                    else
                        formatText();
                } else if (noteContentText.hasSelection()) {
                    cont++;
                    if (cont == 1)
                        stopThread1();
                    else
                        formatText();
                }
                return false;
            }
        };
        noteContentText.setOnTouchListener(listener);
        noteTitleText.setOnTouchListener(listener);
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
     * Start of the text modifications section
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
        cont=0;
    }

    /**
     * Start of the threads section
     **/

    public void firstThread() {
        first = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                firstThreadVal.setText(String.valueOf(HRSActivity.mHrmValue));
                                //stopThread1();
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
        if (HRSActivity.mHrmValue > 600 && HRSActivity.mHrmValue < 900) {
            Snackbar.make(getCurrentFocus(), "HIGHLIGHT", Snackbar.LENGTH_INDEFINITE).show();
            mode = "hl";
            secondThread();
            first.interrupt();
        } else if (HRSActivity.mHrmValue > 300 && HRSActivity.mHrmValue < 600) {
            Snackbar.make(getCurrentFocus(), "UNDERLINE", Snackbar.LENGTH_INDEFINITE).show();
            mode = "ul";
            secondThread();
            first.interrupt();
        } else if (HRSActivity.mHrmValue < 300 && HRSActivity.mHrmValue > 50) {
            Snackbar.make(getCurrentFocus(), "DELETE", Snackbar.LENGTH_INDEFINITE).show();
            mode = "dl";
            secondThread();
            first.interrupt();
        } else if (HRSActivity.mHrmValue > 900) {
            Snackbar.make(getCurrentFocus(), "SELECT", Snackbar.LENGTH_INDEFINITE).show();
            mode = "sl";
        }
    }

    public void secondThread() {
        second = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                secondThreadVal.setText(String.valueOf(HRSActivity.mHrmValue));
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
        if (Float.valueOf(secondThreadVal.getText().toString()) < 50) {
            cont = 0;
            firstThread();
            second.interrupt();
        }
    }

    public void formatText() {
        if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("sl")) {
            selectText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("ul")) {
            boldItalicText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("hl")) {
            highlightText();
        } else if ((noteTitleText.hasSelection() || noteContentText.hasSelection()) && mode.equals("dl")) {
            deleteText();
        }
    }

}

