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

import android.text.Html;
import android.graphics.drawable.BitmapDrawable;

import android.util.Log;

import java.util.Arrays;

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
            
            Log.d("EditNoteActivity", "Loading note content: " + currentNote.getContent());
            if (currentNote.getImagePaths() != null) {
                Log.d("EditNoteActivity", "Image paths: " + currentNote.getImagePaths().toString());
            }
            
            // 使用 Html.fromHtml 来加载富文本内容
            Spanned spannedText = Html.fromHtml(currentNote.getContent(), Html.FROM_HTML_MODE_COMPACT, 
                source -> {
                    try {
                        Log.d("EditNoteActivity", "Loading image from source: " + source);
                        // 从应用私有目录加载图片
                        File imageFile = new File(getFilesDir(), source);
                        Log.d("EditNoteActivity", "Image file path: " + imageFile.getAbsolutePath());
                        Log.d("EditNoteActivity", "Image file exists: " + imageFile.exists());
                        
                        if (imageFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            if (bitmap != null) {
                                Log.d("EditNoteActivity", "Successfully loaded bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                                
                                // 计算压缩后的尺寸
                                int maxWidth = getResources().getDisplayMetrics().widthPixels - 200;
                                int maxHeight = getResources().getDisplayMetrics().heightPixels / 2;
                                
                                float scaleWidth = (float) maxWidth / bitmap.getWidth();
                                float scaleHeight = (float) maxHeight / bitmap.getHeight();
                                float scale = Math.min(scaleWidth, scaleHeight);
                                
                                int newWidth = Math.round(bitmap.getWidth() * scale);
                                int newHeight = Math.round(bitmap.getHeight() * scale);
                                
                                // 压缩图片
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                                BitmapDrawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
                                drawable.setBounds(0, 0, newWidth, newHeight);
                                
                                // 创建新的 PathImageSpan
                                PathImageSpan imageSpan = new PathImageSpan(drawable, source);
                                SpannableString spannableString = new SpannableString("\uFFFC");
                                spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return drawable;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("EditNoteActivity", "Error loading image: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }, null);
            binding.contentEditText.setText(spannedText);
        }
    }

    private void saveNote() {
        String title = binding.titleEditText.getText().toString().trim();
        Editable content = binding.contentEditText.getText();
        
        Log.d("EditNoteActivity", "Saving note...");
        
        // 确保标题不为空
        if (title.isEmpty()) {
            title = "未命名笔记";
        }
        
        if (currentNote == null) {
            currentNote = new Note();
            currentNote.setCreateTime(System.currentTimeMillis());
            currentNote.setImagePaths(new ArrayList<>());
            Log.d("EditNoteActivity", "Created new note");
        }
        
        ImageSpan[] imageSpans = content.getSpans(0, content.length(), ImageSpan.class);
        Log.d("EditNoteActivity", "Found " + imageSpans.length + " image spans");
        
        StringBuilder htmlContent = new StringBuilder();
        int lastIndex = 0;
        
        // 处理文本和图片
        for (ImageSpan span : imageSpans) {
            int start = content.getSpanStart(span);
            int end = content.getSpanEnd(span);
            
            // 添加图片前的文本
            String text = content.subSequence(lastIndex, start).toString();
            if (!text.isEmpty()) {
                // 将换行符替换为 <br> 标签
                text = text.replace("\n", "<br>");
                htmlContent.append(text);
            }
            
            // 添加图片标签
            if (span instanceof PathImageSpan) {
                String imagePath = ((PathImageSpan) span).getImagePath();
                htmlContent.append("<img src=\"").append(imagePath).append("\">");
            } else {
                // 如果是普通的 ImageSpan，尝试从 currentNote 的 imagePaths 中获取
                int currentImageIndex = Arrays.asList(imageSpans).indexOf(span);
                if (currentNote.getImagePaths() != null && currentImageIndex < currentNote.getImagePaths().size()) {
                    String imagePath = currentNote.getImagePaths().get(currentImageIndex);
                    htmlContent.append("<img src=\"").append(imagePath).append("\">");
                }
            }
            
            lastIndex = end;
        }
        
        // 添加剩余的文本
        if (lastIndex < content.length()) {
            String text = content.subSequence(lastIndex, content.length()).toString();
            if (!text.isEmpty()) {
                // 将换行符替换为 <br> 标签
                text = text.replace("\n", "<br>");
                htmlContent.append(text);
            }
        }
        
        Log.d("EditNoteActivity", "Final HTML content: " + htmlContent.toString());
        
        currentNote.setTitle(title); 
        currentNote.setContent(htmlContent.toString());
        currentNote.setUpdateTime(System.currentTimeMillis());
        
        try {
            noteDao.saveNote(currentNote);
            Log.d("EditNoteActivity", "Note saved successfully");
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e("EditNoteActivity", "Error saving note: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String convertToHtml(Editable content) {
        // 处理图片和文本
        StringBuilder html = new StringBuilder();
        ImageSpan[] imageSpans = content.getSpans(0, content.length(), ImageSpan.class);
        
        int lastIndex = 0;
        for (ImageSpan span : imageSpans) {
            int start = content.getSpanStart(span);
            int end = content.getSpanEnd(span);
            
            // 添加图片前的文本
            String text = content.subSequence(lastIndex, start).toString();
            html.append(Html.escapeHtml(text));
            
            // 保存图片到应用私有目录并添加图片标签
            String imagePath = saveImageToFile(((BitmapDrawable)span.getDrawable()).getBitmap());
            html.append("<img src=\"").append(imagePath).append("\">");
            
            lastIndex = end;
        }
        
        // 添加最后的文本
        if (lastIndex < content.length()) {
            String text = content.subSequence(lastIndex, content.length()).toString();
            html.append(Html.escapeHtml(text));
        }
        
        return html.toString();
    }

    private String saveImageToFile(Bitmap bitmap) {
        try {
            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
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
            .setMessage("确定要删除这篇笔记？")
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
            File imageFile = new File(getFilesDir(), "images");
            if (!imageFile.exists()) {
                imageFile.mkdirs();
            }
            File destFile = new File(imageFile, fileName);
            
            // 复制图片文件
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            // 加载并压缩图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath(), options);
            
            if (bitmap != null) {
                // 计算压缩后的尺寸
                int maxWidth = getResources().getDisplayMetrics().widthPixels - 200; // 增加左右边距
                int maxHeight = getResources().getDisplayMetrics().heightPixels / 2; // 限制最大高度

                float scaleWidth = (float) maxWidth / bitmap.getWidth();
                float scaleHeight = (float) maxHeight / bitmap.getHeight();
                float scale = Math.min(scaleWidth, scaleHeight); // 取最小的缩放比例，保持宽高比

                int newWidth = Math.round(bitmap.getWidth() * scale);
                int newHeight = Math.round(bitmap.getHeight() * scale);

                // 压缩图片
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                bitmap.recycle();
                
                // 插入图片到编辑器
                String imagePath = "images/" + fileName;
                SpannableString spannableString = new SpannableString("\uFFFC");
                PathImageSpan imageSpan = new PathImageSpan(this, resizedBitmap, imagePath);
                spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                
                // 在当前光标位置插入图片
                int start = Math.max(binding.contentEditText.getSelectionStart(), 0);
                binding.contentEditText.getText().insert(start, spannableString);
                
                // 保存图片路径到 Note 对象
                if (currentNote.getImagePaths() == null) {
                    currentNote.setImagePaths(new ArrayList<>());
                }
                currentNote.getImagePaths().add("images/" + fileName);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "插入图片失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Editable content = binding.contentEditText.getText();
        
        // 如果是新建笔记
        if (currentNote == null) {
            return !currentTitle.isEmpty() || content.length() > 0;
        }
        
        // 如果是编辑已有笔记，比较内容是否有变化
        String savedContent = currentNote.getContent()
                .replace("&#10;", "\n")  // 处理 HTML 实体
                .replace("<br>", "\n")   // 处理 HTML 换行标签
                .replaceAll("<img[^>]+>", "\uFFFC"); // 将图片标签替换为占位符
                
        String currentText = content.toString();
        
        return !currentTitle.equals(currentNote.getTitle()) ||
               !currentText.equals(savedContent);
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
