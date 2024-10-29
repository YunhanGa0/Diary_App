package com.example.diary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.Manifest;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.diary.databinding.ActivityEditNoteBinding;
import com.example.diary.dialog.MoodPickerDialog;
import com.example.diary.model.Note;
import com.example.diary.dialog.ColorPickerDialog;
import com.example.diary.widget.RichEditText;
import com.example.diary.db.NoteDao;

import java.io.File;
import android.graphics.Bitmap;

import java.util.ArrayList;

import android.graphics.BitmapFactory;

public class EditNoteActivity extends AppCompatActivity implements RichEditText.OnFormatStateChangeListener {
    private ActivityEditNoteBinding binding;
    private Note currentNote;
    private NoteDao noteDao;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_AUDIO_PERMISSION = 2;
    private static final int REQUEST_IMAGE_PERMISSION = 3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置返按钮
        setSupportActionBar(binding.toolbar);  // 确保你的布局中 Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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

        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImagePermissionAndPick();
            }
        });

        // 设置心情按钮点击事件
        binding.btnMood.setOnClickListener(v -> showMoodPicker());
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
        
        // 如果标题和内容都为空，直接退出不保存
        if (title.isEmpty() && content.isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        
        // 标题为空时使用默认标题
        if (title.isEmpty()) {
            title = "未命名笔记";
        }
        
        // 如果是新建笔记
        if (currentNote == null) {
            currentNote = new Note();
            currentNote.setCreateTime(System.currentTimeMillis());
        }
        
        // 更新笔记内容
        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setUpdateTime(System.currentTimeMillis());
        
        // 保存到数据库
        try {
            noteDao.saveNote(currentNote);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {  // 处理左上角返回按钮
            onBackPressed();  // 调用同的返回逻辑
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

     private void checkImagePermissionAndPick() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_IMAGE_PERMISSION);
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "选择图片失败：" + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    // 处理选中的图片
                    insertImage(imageUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "插入图片失败：" + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertImage(Uri imageUri) {
        try {
            // 将图片复制到应用私有目录
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            // 保存图片路径
            if (currentNote != null && currentNote.getImagePaths() == null) {
                currentNote.setImagePaths(new ArrayList<>());
            }
            if (currentNote != null) {
                currentNote.getImagePaths().add(imageFile.getAbsolutePath());
            }
            
            // 插入图片到编辑器
            int start = Math.max(0, binding.contentEditText.getSelectionStart());
            SpannableString imageSpan = new SpannableString(" ");
            
            // 加载并压缩图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            
            // 计算压缩比例
            int maxWidth = getResources().getDisplayMetrics().widthPixels;
            int scale = 1;
            while (options.outWidth / scale > maxWidth) {
                scale *= 2;
            }
            
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            
            // 创建图片视图
            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageView.setMaxWidth(maxWidth);
            imageView.setImageBitmap(bitmap);
            
            imageSpan.setSpan(new ImageSpan(this, bitmap), 0, 1, 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            Editable editable = binding.contentEditText.getText();
            if (editable != null) {
                editable.insert(start, "\n");
                editable.insert(start + 1, imageSpan);
                editable.insert(start + 2, "\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "处理图片失败：" + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void copyImageToFile(Uri sourceUri, File destFile) throws IOException {
        InputStream in = getContentResolver().openInputStream(sourceUri);
        OutputStream out = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }

    // 添加返回键保存提示
    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            new AlertDialog.Builder(this)
                .setTitle("保存更改")
                .setMessage("是否保存更改？")
                .setPositiveButton("保存", (dialog, which) -> {
                    saveNote();
                })
                .setNegativeButton("不保存", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();  // 直接退出，不保存
                })
                .setNeutralButton("取消", null)
                .show();
        } else {
            setResult(RESULT_CANCELED);
            finish();  // 如果没有更改，直接退出
        }
    }

    // 检查是否有未保存的更改
    private boolean hasUnsavedChanges() {
        String currentTitle = binding.titleEditText.getText().toString().trim();
        String currentContent = binding.contentEditText.getText().toString().trim();
        
        // 如果是新建笔记
        if (currentNote == null) {
            // 只有当标题或内容不为空时，才认为有未保存的更改
            return !currentTitle.isEmpty() || !currentContent.isEmpty();
        }
        
        // 如果是编辑已有笔记，比较内容是否有变化
        return !currentTitle.equals(currentNote.getTitle()) ||
               !currentContent.equals(currentNote.getContent());
    }

    private void showMoodPicker() {
        MoodPickerDialog dialog = new MoodPickerDialog(this, mood -> {
            if (currentNote != null) {
                currentNote.setMood(mood);
                showMoodToast(mood);
            }
        });
        dialog.show();
    }

    private void showMoodToast(int mood) {
        String moodText;
        switch (mood) {
            case 0:
                moodText = "非常伤心";
                break;
            case 1:
                moodText = "伤心";
                break;
            case 2:
                moodText = "一般";
                break;
            case 3:
                moodText = "开心";
                break;
            case 4:
                moodText = "非常开心";
                break;
            case 5:
                moodText = "生气";
                break;
            default:
                moodText = "未知心情";
        }
        Toast.makeText(this, "您选择了：" + moodText, Toast.LENGTH_SHORT).show();
    }
}
