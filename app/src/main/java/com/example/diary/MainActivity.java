package com.example.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.diary.adapter.NoteAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private FloatingActionButton fabNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void initViews() {
        // 初始化RecyclerView
        recyclerView = findViewById(R.id.notesRecyclerView);
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化悬浮按钮
        fabNewNote = findViewById(R.id.fabNewNote);
    }

    private void setupListeners() {
        // 设置笔记点击监听
        adapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("note_id", note.getId());
            startActivity(intent);
        });

        // 设置新建笔记按钮点击监听
        fabNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            startActivity(intent);
        });
    }

    private void loadNotes() {
        // TODO: 从数据库加载笔记列表
        // 临时测试代码
        List<Note> testNotes = new ArrayList<>();
        Note note = new Note();
        note.setTitle("测试笔记");
        note.setContent("这是一个测试笔记");
        testNotes.add(note);
        
        adapter.setNotes(testNotes);
    }
}
