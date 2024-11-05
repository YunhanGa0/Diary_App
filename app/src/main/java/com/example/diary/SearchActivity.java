package com.example.diary;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.diary.adapter.NoteAdapter;
import com.example.diary.databinding.ActivitySearchBinding;
import com.example.diary.db.NoteDao;
import com.example.diary.model.Note;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private NoteAdapter adapter;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteDao = new NoteDao(this);
        setupViews();
        setupListeners();
        
        showEmptyState();
    }

    private void showEmptyState() {
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.emptyView.emptyText.setText("输入关键词开始搜索");
        adapter.setNotes(new ArrayList<>());
    }

    private void setupViews() {
        adapter = new NoteAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.searchEditText.requestFocus();
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.clearButton.setOnClickListener(v -> binding.searchEditText.setText(""));

        binding.moodVeryHappy.setOnClickListener(v -> searchNotesByMood(4));
        binding.moodHappy.setOnClickListener(v -> searchNotesByMood(3));
        binding.moodNeutral.setOnClickListener(v -> searchNotesByMood(2));
        binding.moodSad.setOnClickListener(v -> searchNotesByMood(1));
        binding.moodVerySad.setOnClickListener(v -> searchNotesByMood(0));
        binding.moodAngry.setOnClickListener(v -> searchNotesByMood(5));

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchNotes(s.toString().trim());
                binding.clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchNotes(String query) {
        if (query.isEmpty()) {
            showEmptyState();
            return;
        }
        
        List<Note> searchResults = noteDao.searchNotes(query);
        adapter.setNotes(searchResults);
        
        if (searchResults.isEmpty()) {
            binding.emptyView.getRoot().setVisibility(View.VISIBLE);
            binding.emptyView.emptyText.setText("未找到匹配的笔记");
        } else {
            binding.emptyView.getRoot().setVisibility(View.GONE);
        }
    }

    private void searchNotesByMood(int mood) {
        List<Note> results = noteDao.getNotesByMood(mood);
        adapter.setNotes(results);
        
        if (results.isEmpty()) {
            binding.emptyView.getRoot().setVisibility(View.VISIBLE);
            binding.emptyView.emptyText.setText("没有找到相应心情的笔记");
        } else {
            binding.emptyView.getRoot().setVisibility(View.GONE);
        }
    }
} 