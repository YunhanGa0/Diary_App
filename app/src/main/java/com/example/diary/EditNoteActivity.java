package com.example.diary;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diary.databinding.ActivityEditNoteBinding;
import com.example.diary.model.Note;
import com.example.diary.dialog.ColorPickerDialog;

public class EditNoteActivity extends AppCompatActivity {
    private ActivityEditNoteBinding binding;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupFormatButtons();

        // 获取传入的笔记ID
        long noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId != -1) {
            loadNote(noteId);
        } else {
            currentNote = new Note();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void setupFormatButtons() {
        binding.boldButton.setOnClickListener(v -> {
            binding.contentEditText.toggleBold();
        });
        
        binding.italicButton.setOnClickListener(v -> {
            binding.contentEditText.toggleItalic();
        });
        
        binding.underlineButton.setOnClickListener(v -> {
            binding.contentEditText.toggleUnderline();
        });
        
        binding.textSizeButton.setOnClickListener(v -> showTextSizeDialog());
        
        binding.textColorButton.setOnClickListener(v -> showColorPicker());
    }

    private void showTextSizeDialog() {
        String[] sizes = new String[]{"小", "正常", "大", "特大"};
        new AlertDialog.Builder(this)
            .setTitle("选择字号")
            .setItems(sizes, (dialog, which) -> {
                Toast.makeText(this, "选择了: " + sizes[which], Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void showColorPicker() {
        ColorPickerDialog dialog = new ColorPickerDialog(this, color -> {
            binding.contentEditText.setTextColor(color);
        });
        dialog.show();
    }

    private void loadNote(long noteId) {
        // TODO: 从数据库加载笔记
        // 临时测试代码
        currentNote = new Note();
        currentNote.setId(noteId);
        binding.titleEditText.setText(currentNote.getTitle());
        binding.contentEditText.setText(currentNote.getContent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
