package com.example.diary;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Handler;
import android.os.Looper;
import static com.example.diary.utils.Constants.PREF_NAME;
import static com.example.diary.utils.Constants.KEY_DARK_MODE;

public class DiaryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 读取深色模式设置并应用
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
} 