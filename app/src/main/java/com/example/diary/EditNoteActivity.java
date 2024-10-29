package com.example.diary;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diary.databinding.ActivityEditNoteBinding;
import com.example.diary.model.Note;
import com.example.diary.dialog.ColorPickerDialog;
import com.example.diary.widget.RichEditText;
import com.example.diary.db.NoteDao;

public class EditNoteActivity extends AppCompatActivity implements RichEditText.OnFormatStateChangeListener {
    private ActivityEditNoteBinding binding;
    private Note currentNote;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteDao = new NoteDao(this);
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
        // 设置格式状态变化监听器
        binding.contentEditText.setOnFormatStateChangeListener(this);

        // 设置按钮点击监听
        binding.boldButton.setOnClickListener(v -> {
            if (hasTextSelected()) {
                binding.contentEditText.toggleBold();
                updateFormatButtonStates();
            } else {
                Toast.makeText(this, "请先选择要格式化的文本", Toast.LENGTH_SHORT).show();
            }
        });
        
        binding.italicButton.setOnClickListener(v -> {
            if (hasTextSelected()) {
                binding.contentEditText.toggleItalic();
                updateFormatButtonStates();
            } else {
                Toast.makeText(this, "请先选择要格式化的文本", Toast.LENGTH_SHORT).show();
            }
        });
        
        binding.underlineButton.setOnClickListener(v -> {
            if (hasTextSelected()) {
                binding.contentEditText.toggleUnderline();
                updateFormatButtonStates();
            } else {
                Toast.makeText(this, "请先选择要格式化的文本", Toast.LENGTH_SHORT).show();
            }
        });
        
        binding.textSizeButton.setOnClickListener(v -> showTextSizeDialog());
        
        binding.textColorButton.setOnClickListener(v -> showColorPicker());

        // 添加选择监听
        binding.contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFormatButtonStates();
            }
        });
    }

    private boolean hasTextSelected() {
        return binding.contentEditText.getSelectionStart() != binding.contentEditText.getSelectionEnd();
    }

    @Override
    public void onFormatStateChanged() {
        updateFormatButtonStates();
    }

    private void updateFormatButtonStates() {
        // 更新按钮状态
        binding.boldButton.setSelected(binding.contentEditText.isBold());
        binding.italicButton.setSelected(binding.contentEditText.isItalic());
        binding.underlineButton.setSelected(binding.contentEditText.isUnderlined());
        
        // 可以根据当前字体大小更新字体大小按钮的状态
        int currentSize = binding.contentEditText.getCurrentTextSize();
        binding.textSizeButton.setSelected(currentSize != RichEditText.TEXT_SIZE_NORMAL);
    }

    private void showTextSizeDialog() {
        String[] sizes = new String[]{"小", "正常", "大", "特大"};
        new AlertDialog.Builder(this)
            .setTitle("选择字号")
            .setItems(sizes, (dialog, which) -> {
                int size;
                switch (which) {
                    case 0:
                        size = RichEditText.TEXT_SIZE_SMALL;
                        break;
                    case 1:
                        size = RichEditText.TEXT_SIZE_NORMAL;
                        break;
                    case 2:
                        size = RichEditText.TEXT_SIZE_LARGE;
                        break;
                    case 3:
                        size = RichEditText.TEXT_SIZE_HUGE;
                        break;
                    default:
                        size = RichEditText.TEXT_SIZE_NORMAL;
                }
                
                if (hasTextSelected()) {
                    binding.contentEditText.setTextSize(size);
                    updateFormatButtonStates();
                } else {
                    Toast.makeText(this, "请先选择要格式化的文本", Toast.LENGTH_SHORT).show();
                }
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
        currentNote = noteDao.getNoteById(noteId);
        if (currentNote != null) {
            binding.titleEditText.setText(currentNote.getTitle());
            binding.contentEditText.setText(currentNote.getContent());
        }
    }

    private void saveNote() {
        String title = binding.titleEditText.getText().toString().trim();
        String content = binding.contentEditText.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "标题和内容不能都为空", Toast.LENGTH_SHORT).show();
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setUpdateTime(System.currentTimeMillis());

        if (currentNote.getId() == 0) {
            // 新建笔记
            long id = noteDao.insertNote(currentNote);
            currentNote.setId(id);
        } else {
            // 更新笔记
            noteDao.updateNote(currentNote);
        }

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_save) {
            saveNote();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("删除笔记")
            .setMessage("确定要删除这篇笔记吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                deleteNote();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void deleteNote() {
        if (currentNote != null && currentNote.getId() != 0) {
            noteDao.deleteNote(currentNote.getId());
            Toast.makeText(this, "笔记已删除", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
