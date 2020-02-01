/*
 * Copyright (C) 2015-2016 Aurelitec
 * http://www.aurelitec.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aurelitec.injuredpixels;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * The InjuredPixels main activity.
 */
public class InjuredPixelsActivity extends InjuredPixelsBaseActivity {

//region Fields

    /**
     * Instance state key for saving the Full Screen current setting.
     */
    private static final String STATE_FULL_SCREEN = "full_screen";

    /**
     * The gesture detector.
     */
    private GestureDetector mDoubleClickDetector;

//endregion

//region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injured_pixels);

        // Find views and init the overflow menu and the gesture detector
        findInitViews();
        initOverflowMenu(R.menu.popup_menu);
        mDoubleClickDetector = new GestureDetector(this, new MyGestureListener());

        // Go full screen immersive sticky
        Utils.setCrossFadeRotationAnimation(getWindow());
        Utils.setImmersiveFullScreen(getWindow());
    }

    /**
     * Set immersive full screen mode when window gets focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Utils.setImmersiveFullScreen(getWindow());
        }
    }

    /**
     * Load and apply preferences when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    /**
     * Restore the full screen mode when the instance state is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            setFullScreen(savedInstanceState.getBoolean(STATE_FULL_SCREEN));
        }
    }

    /**
     * Connect the double click gesture detector on touch events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDoubleClickDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Update the color when the Named Colors Activity returns.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ColorPickerActivity.REQUEST_NAMED_COLOR) {
            if (resultCode == RESULT_OK) {
                int resultColor = data.getIntExtra(ColorPickerActivity.EXTRA_SELECTED_COLOR, 0);
                if (resultColor != 0) {
                    setCustomColor(resultColor);
                    savePreferences();
                }
            }
        }
    }

    /**
     * Save preferences when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    /**
     * Save the full screen mode when the instance state is saved.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_FULL_SCREEN, isFullScreen());
        super.onSaveInstanceState(outState);
    }

//endregion

//region Activity UI Controls

    /**
     * Update the color when a new color button is clicked.
     */
    public void colorButtonClick(View view) {
        setTestColor(view);
    }

    /**
     * Override onKeyDown to cycle through colors when the user presses the volume buttons.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                previousColor();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                nextColor();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Disable the audible beep when pressing the Volume buttons.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) ||
                super.onKeyUp(keyCode, event);
    }

    /**
     * Toggle full screen mode when the user double taps the screen.
     */
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setFullScreen(!isFullScreen());
            return true;
        }
    }

//endregion

//region Popup Menu

    /**
     * Show the popup menu when the overflow button is clicked.
     */
    @SuppressWarnings("UnusedParameters")
    public void menuButtonClick(View view) {
        showOverflowMenu();
    }

    /**
     * "Set custom color" menu item: open a color picker dialog activity to allow the user to pick a custom color.
     */
    @SuppressWarnings("UnusedParameters")
    public void actionSetCustomColor(MenuItem item) {
        Intent intent = new Intent(this, ColorPickerActivity.class);
        intent.putExtra(ColorPickerActivity.EXTRA_SELECTED_COLOR, getCustomColor());
        startActivityForResult(intent, ColorPickerActivity.REQUEST_NAMED_COLOR);
    }

    /**
     * "Max brightness" menu item: toggle screen full brightness.
     */
    public void actionMaxBrightness(MenuItem item) {
        toggleMaxBrightness();
        item.setChecked(isMaxBrightness());
        Utils.setFullBrightness(getWindow(), isMaxBrightness());
    }

    /**
     * "Rate app" menu item: open the app page in Google Play to allow the user to rate the app.
     */
    @SuppressWarnings("UnusedParameters")
    public void actionRate(MenuItem item) {
        Utils.viewUrl(InjuredPixelsActivity.this, getString(R.string.action_rate_url));
    }

    /**
     * "Help" menu item: open the app home page to allow the user to read more info about the app.
     */
    @SuppressWarnings("UnusedParameters")
    public void actionHelp(MenuItem item) {
        Utils.viewUrl(InjuredPixelsActivity.this, getString(R.string.action_help_url));
    }

//endregion
}