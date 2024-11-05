package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diary.databinding.ActivityMainBinding;
import com.example.diary.db.NoteDao;
import com.example.diary.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.diary.adapter.NoteAdapter;
import com.google.android.material.search.SearchBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteDao = new NoteDao(this);
        initViews();
        setupListeners();
        setupBottomNavigation();
        setupToolbar();
    }

    private void initViews() {
        adapter = new NoteAdapter();
        binding.recyclerView.setAdapter(adapter);
        
        // 使用 StaggeredGridLayoutManager 替换 LinearLayoutManager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        
        // 移除分割线
        // binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchNotes(String query) {
        if (query.isEmpty()) {
            loadNotes();  // 显示所有笔记
            binding.emptyView.getRoot().setVisibility(View.GONE);
        } else {
            List<Note> searchResults = noteDao.searchNotes(query);
            adapter.setNotes(searchResults);
            
            // 更新空状态视图
            if (searchResults.isEmpty()) {
                binding.emptyView.getRoot().setVisibility(View.VISIBLE);
                binding.emptyView.emptyText.setText("未找到匹配的笔记");
            } else {
                binding.emptyView.getRoot().setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        // 从数据库加载所有笔记
        List<Note> notes = noteDao.getAllNotes();
        adapter.setNotes(notes);
        
        // 如果没有笔记，显示空状态
        if (notes.isEmpty()) {
            binding.emptyView.getRoot().setVisibility(View.VISIBLE);
            binding.emptyView.emptyText.setText("还没有笔记，点击底部新建按钮添加");
        } else {
            binding.emptyView.getRoot().setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmDialog(Note note) {
        new AlertDialog.Builder(this)
            .setTitle("删除笔记")
            .setMessage("确定要删除这篇笔记吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                noteDao.deleteNote(note.getId());
                loadNotes();  // 重新加载笔记列表
                Toast.makeText(this, "笔记已删除", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void setupListeners() {
        adapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("note_id", note.getId());
            startActivity(intent);
        });

        adapter.setOnNoteLongClickListener(this::showDeleteConfirmDialog);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // 已经在主页，不需要操作
                return true;
            } else if (itemId == R.id.navigation_new_note) {
                Intent intent = new Intent(this, EditNoteActivity.class);
                startActivity(intent);
                return false; // 不选中该项
            } else if (itemId == R.id.navigation_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return false; // 不选中该项
            }
            return false;
        });
    }

    private void setupToolbar() {
        binding.searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

    }
}
