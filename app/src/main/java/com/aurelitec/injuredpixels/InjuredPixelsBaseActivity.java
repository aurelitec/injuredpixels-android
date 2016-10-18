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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

/**
 * The InjuredPixels main activity: base activity class.
 */
@SuppressLint("Registered")
public class InjuredPixelsBaseActivity extends Activity {

//region Fields

    /**
     * Default test color index.
     */
    private static final int DEFAULT_COLOR_INDEX = 0;

    /**
     * Default custom color.
     */
    private static final int DEFAULT_CUSTOM_COLOR = Color.MAGENTA;

    /**
     * Primary test colors array.
     */
    private static final int[] primaryColors = {Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, DEFAULT_CUSTOM_COLOR};

    /**
     * The current test color index.
     */
    private int mColorIndex = DEFAULT_COLOR_INDEX;

    /**
     * The current full screen status.
     */
    private boolean mFullScreen = false;

    /**
     * Current brightness mode.
     */
    private boolean mMaxBrightness = false;

    /**
     * The root view.
     */
    private View mRootView;

    /**
     * The color buttons panel.
     */
    private ViewGroup mColorButtonsPanel;

    /**
     * The custom color button.
     */
    private Button mCustomColorButton;

    /**
     * The overflow menu button.
     */
    private Button mOverflowMenuButton;

    /**
     * The overflow popup menu.
     */
    private PopupMenu mOverflowMenu;

    /**
     * The Max brightness menu item.
     */
    private MenuItem mMaxBrightnessMenuItem;

    /**
     * The menu tip text view.
     */
    private ViewGroup mTipLayout;
    private TextView mTipIconTextView;
    private TextView mTipTextView;

//endregion

//region Views and Menu Initialization

    /**
     * Find and init the views from the layout.
     */
    void findInitViews() {
        mRootView = findViewById(R.id.root);
        mOverflowMenuButton = (Button) findViewById(R.id.popupMenuButton);
        mCustomColorButton = (Button) findViewById(R.id.custom_color_button);
        mColorButtonsPanel = (ViewGroup) findViewById(R.id.menu);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            Utils.setButtonBackground(this, R.id.black_button, Utils.getColor(this, R.color.black));
            Utils.setButtonBackground(this, R.id.white_button, Utils.getColor(this, R.color.white));
            Utils.setButtonBackground(this, R.id.red_button, Utils.getColor(this, R.color.red));
            Utils.setButtonBackground(this, R.id.green_button, Utils.getColor(this, R.color.green));
            Utils.setButtonBackground(this, R.id.blue_button, Utils.getColor(this, R.color.blue));
        }

        // Init the tip message with its embedded HTML tags
        mTipLayout = (ViewGroup) findViewById(R.id.tip_layout);
        mTipIconTextView = (TextView) findViewById(R.id.tip_icon_text_view);
        mTipTextView = (TextView) findViewById(R.id.tip_text_view);
        mTipTextView.setText(Utils.fromHtml(getString(R.string.menu_tip)));
    }

    /**
     * Init the overflow menu.
     */
    @SuppressWarnings("SameParameterValue")
    void initOverflowMenu(int menuRes) {
        mOverflowMenu = new PopupMenu(this, mOverflowMenuButton);
        mOverflowMenu.inflate(menuRes);
        mMaxBrightnessMenuItem = mOverflowMenu.getMenu().findItem(R.id.action_max_brightness);

        // Set the icon font to the buttons
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.font_icon));
        mOverflowMenuButton.setTypeface(font);

        // The tip icon also comes from the icon font, so make an exception and set its font here
        mTipIconTextView.setTypeface(font);
    }

    /**
     * Show the overflow menu.
     */
    void showOverflowMenu() {
        if (mOverflowMenu != null) mOverflowMenu.show();
    }

//endregion

//region Color Functionality

    /**
     * Goes to a new primary or custom test color.
     */
    private void setTestColor(int index) {
        if ((index >= 0) && (index < primaryColors.length)) {
            mColorIndex = index;
            int color = primaryColors[mColorIndex];

            // Fill the screen with the new test color
            mRootView.setBackgroundColor(color);

            int contrastColor = Utils.getContrastColor(color);
            mOverflowMenuButton.setTextColor(contrastColor);
            mTipIconTextView.setTextColor(contrastColor);
            mTipTextView.setTextColor(contrastColor);

            // Fill the navigation bar with the new test color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(color);
            }
        }
    }

    /**
     * Goes to the test color that corresponds to a color button.
     */
    void setTestColor(View colorButton) {
        int index = mColorButtonsPanel.indexOfChild(colorButton);
        if (index >= 0) setTestColor(index);
    }

    /**
     * Cycles to the next color.
     */
    void nextColor() {
        setTestColor(mColorIndex < primaryColors.length - 1 ? mColorIndex + 1 : 0);
    }

    /**
     * Cycles to the previous color.
     */
    void previousColor() {
        setTestColor(mColorIndex > 0 ? mColorIndex - 1 : primaryColors.length - 1);
    }

    /**
     * Gets the current custom color.
     */
    int getCustomColor() {
        return primaryColors[primaryColors.length - 1];
    }

    /**
     * Sets and apply a new custom color.
     */
    void setCustomColor(int color) {
        int index = primaryColors.length - 1;
        primaryColors[index] = color;
        setTestColor(index);
        Utils.setButtonBackground(mCustomColorButton, color);
        mCustomColorButton.setTextColor(Utils.getContrastColor(color));
    }

//endregion

//region Extra Functionality

    /**
     * Is the full screen mode on?
     */
    boolean isFullScreen() {
        return mFullScreen;
    }

    /**
     * Set and apply a new full screen mode.
     */
    void setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
        int newVisibility = mFullScreen ? View.GONE : View.VISIBLE;

        // Apply the new full screen setting
        mColorButtonsPanel.setVisibility(newVisibility);
        mOverflowMenuButton.setVisibility(newVisibility);
        mTipLayout.setVisibility(newVisibility);
    }

    /**
     * Is max brightness on?
     */
    boolean isMaxBrightness() {
        return mMaxBrightness;
    }

    /**
     * Toggles max brightness.
     */
    void toggleMaxBrightness() {
        mMaxBrightness = !mMaxBrightness;
    }

//endregion

//region Load and Save Preferences

    /**
     * Load the preferences from the Shared Preferences, and apply them.
     */
    void loadPreferences() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Read and set the saved custom color value
        final int customColor = sharedPref.getInt(getString(R.string.pref_custom_color), DEFAULT_CUSTOM_COLOR);
        setCustomColor(customColor);

        // Read and set the saved color index
        final int index = sharedPref.getInt(getString(R.string.pref_color_index), DEFAULT_COLOR_INDEX);
        setTestColor(index);

        // Load and apply the full brightness setting
        mMaxBrightness = sharedPref.getBoolean(getString(R.string.pref_max_brightness), false);
        mMaxBrightnessMenuItem.setChecked(mMaxBrightness);
        Utils.setFullBrightness(getWindow(), mMaxBrightness);
    }

    /**
     * Save the required preferences to the Shared Preferences.
     */
    void savePreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(getString(R.string.pref_color_index), mColorIndex)
                .putInt(getString(R.string.pref_custom_color), getCustomColor())
                .putBoolean(getString(R.string.pref_max_brightness), mMaxBrightness)
                .apply();
    }

//endregion
}
