package com.example.diary;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diary.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: 实现深色模式切换
        });

        binding.autoSaveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: 实现自动保存设置
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 