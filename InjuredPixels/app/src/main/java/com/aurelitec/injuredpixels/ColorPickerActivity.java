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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chiralcode.colorpicker.ColorPicker;

/**
 * The color picker activity.
 */
public class ColorPickerActivity extends Activity {

//region Fields

    /**
     * Intent extra for passing integer color code values.
     */
    public static final String EXTRA_SELECTED_COLOR = "com.aurelitec.injuredpixels.extra.COLOR";

    /**
     * The request code for starting the Named Colors activity.
     */
    public static final int REQUEST_NAMED_COLOR = 1;

    /**
     * The color picker view.
     */
    private ColorPicker mColorPicker;

//endregion

//region Functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        mColorPicker = (ColorPicker) findViewById(R.id.color_picker);

        // Get the current custom color from the InjuredPixels activity, and set the color picker
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SELECTED_COLOR)) {
            int color = intent.getIntExtra(EXTRA_SELECTED_COLOR, 0);
            mColorPicker.setColor(color);
        }
    }

    /**
     * Return the selected color and finish the activity when the user clicks the Set button.
     */
    @SuppressWarnings("UnusedParameters")
    public void setButtonClick(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SELECTED_COLOR, mColorPicker.getColor());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    /**
     * Just finish the activity when the user clicks the cancel button.
     */
    @SuppressWarnings("UnusedParameters")
    public void cancelButtonClick(View view) {
        finish();
    }

//endregion
}
