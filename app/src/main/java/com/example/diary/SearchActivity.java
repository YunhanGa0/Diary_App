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
    }

    private void setupViews() {
        adapter = new NoteAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.searchEditText.requestFocus();
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());

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

        binding.clearButton.setOnClickListener(v -> binding.searchEditText.setText(""));

        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchNotes(binding.searchEditText.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void searchNotes(String query) {
        List<Note> searchResults = noteDao.searchNotes(query);
        adapter.setNotes(searchResults);
        
        if (searchResults.isEmpty()) {
            binding.emptyView.getRoot().setVisibility(View.VISIBLE);
            binding.emptyView.emptyText.setText("未找到匹配的笔记");
        } else {
            binding.emptyView.getRoot().setVisibility(View.GONE);
        }
    }
} 