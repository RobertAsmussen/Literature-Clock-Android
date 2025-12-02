package com.example.literatureclock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Html;
import android.view.View;
import android.view.WindowManager; // Import WindowManager
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView quoteView, metaView, digitalClockView;
    private QuotesDbHelper dbHelper;
    private Handler handler = new Handler();
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Force the screen to stay on using WindowManager flags
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        quoteView = (TextView) findViewById(R.id.quoteText);
        metaView = (TextView) findViewById(R.id.metaText);
        digitalClockView = (TextView) findViewById(R.id.digitalClock);

        // 2. Acquire a Screen WakeLock (replaces Partial WakeLock)
        // SCREEN_BRIGHT_WAKE_LOCK ensures the screen does not dim or turn off.
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "LiteratureClock:TickTock");
        wakeLock.acquire();

        dbHelper = new QuotesDbHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (Exception e) {
            quoteView.setText("DB Error: " + e.getMessage());
        }

        startClockLoop();
    }

    private void startClockLoop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateClock();
                long now = System.currentTimeMillis();
                long nextMinute = (now / 60000 + 1) * 60000;
                handler.postDelayed(this, nextMinute - now);
            }
        });
    }

    private void updateClock() {
        Date now = new Date();
        String dbTime = new SimpleDateFormat("HH:mm", Locale.US).format(now);
        String displayTime = new SimpleDateFormat("h:mm a", Locale.US).format(now).toLowerCase().replace("am", "a.m.").replace("pm", "p.m.");

        digitalClockView.setText(displayTime);

        String[] data = dbHelper.getQuoteForTime(dbTime);

        if (data != null) {
            String rawQuote = data[0];
            String title = data[1];
            String author = data[2];
            String timePhrase = data[3];

            // --- Dynamic Font Sizing ---
            // Decrease font size as text gets longer to prevent overlapping
            int length = rawQuote.length();
            if (length > 500) {
                quoteView.setTextSize(15); // Very small for massive quotes
            } else if (length > 350) {
                quoteView.setTextSize(20); // Small for long quotes
            } else if (length > 200) {
                quoteView.setTextSize(25); // Medium
            } else {
                quoteView.setTextSize(30); // Large (Default)
            }
            // ---------------------------

            if (timePhrase != null && !timePhrase.equals("")) {
                rawQuote = rawQuote.replace(timePhrase, "<b>" + timePhrase + "</b>");
            }

            quoteView.setText(Html.fromHtml(rawQuote));
            metaView.setText(author + "\n" + title);
        } else {
            quoteView.setTextSize(30);
            quoteView.setText("Time marches on...");
            metaView.setText("");
        }

        findViewById(android.R.id.content).invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}