package com.example.literatureclock;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit test for QuotesDbHelper using Robolectric.
 * This test runs on the JVM without needing an Android device.
 *
 * Note: These tests don't require the actual database file since they test
 * the helper's behavior when the database doesn't exist.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class QuotesDbHelperTest {

    @Test
    public void testDatabaseHelperInitialization() {
        // Test that the database helper can be created
        Context context = RuntimeEnvironment.getApplication();
        QuotesDbHelper dbHelper = new QuotesDbHelper(context);

        assertNotNull("Database helper should be initialized", dbHelper);
    }

    @Test
    public void testGetQuoteForTime_withoutDatabase() {
        // Test behavior when database doesn't exist
        Context context = RuntimeEnvironment.getApplication();
        QuotesDbHelper dbHelper = new QuotesDbHelper(context);

        // Should return null when database doesn't exist
        String[] quote = dbHelper.getQuoteForTime("12:00");
        assertNull("Quote should be null when database doesn't exist", quote);
    }

    @Test
    public void testGetQuoteForTime_handlesNullInput() {
        // Test that null input doesn't crash
        Context context = RuntimeEnvironment.getApplication();
        QuotesDbHelper dbHelper = new QuotesDbHelper(context);

        String[] quote = dbHelper.getQuoteForTime(null);
        // Should handle gracefully (either null or exception caught internally)
        assertNull("Quote should be null for null input", quote);
    }

    @Test
    public void testGetQuoteForTime_handlesEmptyString() {
        // Test that empty string doesn't crash
        Context context = RuntimeEnvironment.getApplication();
        QuotesDbHelper dbHelper = new QuotesDbHelper(context);

        String[] quote = dbHelper.getQuoteForTime("");
        // Should handle gracefully
        assertNull("Quote should be null for empty string", quote);
    }

    @Test
    public void testGetQuoteForTime_handlesInvalidFormat() {
        // Test that invalid time formats don't crash
        Context context = RuntimeEnvironment.getApplication();
        QuotesDbHelper dbHelper = new QuotesDbHelper(context);

        String[] invalidTimes = {"invalid", "25:00", "12:60", "abc:def"};

        for (String time : invalidTimes) {
            String[] quote = dbHelper.getQuoteForTime(time);
            // Should handle gracefully without crashing
            assertNull("Quote should be null for invalid time: " + time, quote);
        }
    }
}
