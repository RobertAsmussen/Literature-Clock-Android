package com.example.literatureclock;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class QuotesDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "quotes.db";
    private static final int DB_VERSION = 2;
    private final Context myContext;
    private String DB_PATH;

    public QuotesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }

    public void createDatabase() throws IOException {
        if (!checkInternalDatabase()) {
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    private boolean checkInternalDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        // Check if file exists and has content
        return dbFile.exists() && dbFile.length() > 0;
    }

    private void copyDatabase() throws IOException {
        String outFileName = DB_PATH + DB_NAME;
        File f = new File(outFileName);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        InputStream myInput = null;

        // 1. Try to open from Assets (The Standard Way)
        try {
            myInput = myContext.getAssets().open(DB_NAME);
        } catch (IOException e) {
            // Asset not found?
            // Only then do we check the SD card (Fallback for testing)
            File sdCardFile = new File(Environment.getExternalStorageDirectory(), "quotes.db");
            if (sdCardFile.exists()) {
                myInput = new FileInputStream(sdCardFile);
            } else {
                throw new IOException("quotes.db not found in Assets or SD Card.");
            }
        }

        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public String[] getQuoteForTime(String timeStr) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String[] result = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            File dbFile = new File(myPath);
            if (!dbFile.exists()) return null;

            // CRITICAL FIX:
            // We open with NO_LOCALIZED_COLLATORS.
            // This prevents the "no such table: android_metadata" crash.
            int flags = SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS;

            db = SQLiteDatabase.openDatabase(myPath, null, flags);
            cursor = db.rawQuery("SELECT quote, title, author, time_phrase FROM quotes WHERE time_id = ? ORDER BY RANDOM() LIMIT 1", new String[]{timeStr});

            if (cursor.moveToFirst()) {
                result = new String[]{
                        cursor.getString(0), // Quote
                        cursor.getString(1), // Title
                        cursor.getString(2), // Author
                        cursor.getString(3)  // Time Phrase
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}