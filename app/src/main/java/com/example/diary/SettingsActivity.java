package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.diary.databinding.ActivitySettingsBinding;
import static com.example.diary.utils.Constants.PREF_NAME;
import static com.example.diary.utils.Constants.KEY_DARK_MODE;
import static com.example.diary.utils.Constants.KEY_AUTO_SAVE;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        setupToolbar();
        setupSettings();
        loadSettings();
        setupVersionInfo();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("设置");
        }
    }

    private void setupSettings() {
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveDarkModeSetting(isChecked);
            applyDarkMode(isChecked);
        });

        binding.autoSaveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveAutoSaveSetting(isChecked);
        });
    }

    private void loadSettings() {
        // 加载深色模式设置
        boolean isDarkMode = preferences.getBoolean(KEY_DARK_MODE, false);
        binding.darkModeSwitch.setChecked(isDarkMode);
        
        // 加载自动保存设置
        boolean isAutoSave = preferences.getBoolean(KEY_AUTO_SAVE, true);
        binding.autoSaveSwitch.setChecked(isAutoSave);
    }

    private void saveDarkModeSetting(boolean isDarkMode) {
        preferences.edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .commit();
    }

    private void saveAutoSaveSetting(boolean isAutoSave) {
        preferences.edit().putBoolean(KEY_AUTO_SAVE, isAutoSave).apply();
    }

    private void applyDarkMode(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void setupVersionInfo() {
        try {
            PackageInfo packageInfo = getPackageManager()
                .getPackageInfo(getPackageName(), 0);
            String version = "版本 " + packageInfo.versionName;
            binding.versionText.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static boolean isAutoSaveEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_AUTO_SAVE, true);
    }
} 