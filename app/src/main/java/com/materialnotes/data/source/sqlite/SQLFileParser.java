package com.materialnotes.data.source.sqlite;

import android.util.Log;

import com.materialnotes.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Class to parse  *.sql
 */
class SQLFileParser {

    private static final String TAG = SQLFileParser.class.getSimpleName();
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?:/\\*[^;]*?\\*/)|(?:--[^;]*?$)", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Gets all the statements in a sql file
     *
     * @param stream sql stream
     * @return SQL statements
     */
    static String[] getSQLStatements(InputStream stream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = reader.read()) != -1) sb.append((char) r);
            return COMMENT_PATTERN.matcher(sb).replaceAll(Strings.EMPTY).split(";");
        } catch (IOException ex) {
            Log.e(TAG, "Unable to parse SQL Statements", ex);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Unable to close stream", ex);
                }
            }
        }
    }
}