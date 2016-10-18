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
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Static utility methods.
 */
class Utils {

//region Window Utils

    /**
     * Sets the full screen immersive sticky mode.
     */
    static void setImmersiveFullScreen(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * Sets full brightness policy or normal brightness policy.
     */
    static void setFullBrightness(Window window, boolean fullBrightness) {
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = fullBrightness ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL :
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(lp);
    }

    /**
     * Sets the Crossfade rotation animation.
     */
    static void setCrossFadeRotationAnimation(Window window) {
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE;
        window.setAttributes(winParams);
    }

    /**
     * Wrapper around Html.fromHtml(String) that was deprecated in API 24. Returns displayable styled
     * text from the provided HTML string.
     *
     * @see android.text.Html#fromHtml(String, int)
     * @see android.text.Html#fromHtml(String)
     * @see <a href="https://gist.github.com/AHelloWorldDev/110e6bbf8027783969c314ee96deddad">GitHub Gist</a>
     */
    @SuppressWarnings("deprecation")
    static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

//endregion

//region Button Utils

    /**
     * Sets the background color of a button, before and after LOLLIPOP_MR1.
     */
    static void setButtonBackground(Button button, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            button.setBackgroundTintList(ColorStateList.valueOf(color));
        } else {
            button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            button.setTextColor(getContrastColor(color));
        }
    }

    /**
     * Finds a button view and sets its background color, before and after LOLLIPOP_MR1.
     */
    static void setButtonBackground(Activity activity, int id, int color) {
        Button button = (Button) activity.findViewById(id);
        setButtonBackground(button, color);
    }

//endregion

//region Color Utils

    /**
     * Gets the Black or White contrast color for a specified color.
     */
    static int getContrastColor(int color) {
        // Using the perceived luminance formula: (0.299*red + 0.587*green + 0.114*blue)
        long y = (299L * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 192 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Wrapper around getColor that was deprecated in API 23.
     */
    static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

//endregion

//region Intent Utils

    /**
     * Starts an activity to view an url.
     */
    static void viewUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.feedback_error_url, url), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

//endregion
}
