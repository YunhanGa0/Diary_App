package com.example.diary;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class DiaryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initDarkMode();
    }

    private void initDarkMode() {
        SharedPreferences preferences = getSharedPreferences("diary_settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        int mode = isDarkMode ? 
            AppCompatDelegate.MODE_NIGHT_YES : 
            AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
    }
} 