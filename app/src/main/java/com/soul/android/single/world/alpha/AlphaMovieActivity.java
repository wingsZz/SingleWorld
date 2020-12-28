/*
 * Copyright 2017 Pavel Semak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soul.android.single.world.alpha;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.soul.android.single.world.R;
import com.soul.android.single.world.util.ScreenUtils;

public class AlphaMovieActivity extends AppCompatActivity {
    public static final String FILENAME = "sr.mp4";

    public static final int FIRST_BG_INDEX = 0;
    public static final int BG_ARRAY_LENGTH = 3;


    private AlphaMovieView alphaMovieView;
    private int bgIndex = FIRST_BG_INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alpha_movie);

        alphaMovieView = (AlphaMovieView) findViewById(R.id.video_player);

        int height = ScreenUtils.getScreenRealHeight(this);
        int width = height * 9 / 16;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER;
        alphaMovieView.setLayoutParams(layoutParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        alphaMovieView.onResume();
        alphaMovieView.postDelayed(() -> {
            alphaMovieView.setVideoFromAssets("sr.mp4");
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        alphaMovieView.onPause();
    }
}
