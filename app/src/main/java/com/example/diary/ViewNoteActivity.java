package com.example.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.diary.databinding.ActivityViewNoteBinding;
import com.example.diary.db.NoteDao;
import com.example.diary.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.text.Html.ImageGetter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;

public class ViewNoteActivity extends AppCompatActivity {
    private ActivityViewNoteBinding binding;
    private NoteDao noteDao;
    private Note currentNote;
    private static final int EDIT_NOTE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteDao = new NoteDao(this);
        setupToolbar();
        
        long noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId != -1) {
            loadNote(noteId);
        }

        binding.editFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("note_id", currentNote.getId());
            startActivityForResult(intent, EDIT_NOTE_REQUEST);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void loadNote(long noteId) {
        currentNote = noteDao.getNoteById(noteId);
        if (currentNote != null) {
            binding.titleText.setText(currentNote.getTitle());
            binding.timeText.setText(currentNote.getFormattedTime());
            
            // 创建自定义的ImageGetter来加载图片
            ImageGetter imageGetter = source -> {
                try {
                    Log.d("ViewNoteActivity", "Loading image from source: " + source);
                    // 从应用私有目录加载图片
                    File imageFile = new File(getFilesDir(), source);
                    Log.d("ViewNoteActivity", "Image file path: " + imageFile.getAbsolutePath());
                    Log.d("ViewNoteActivity", "Image file exists: " + imageFile.exists());
                    
                    if (imageFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        if (bitmap != null) {
                            Log.d("ViewNoteActivity", "Successfully loaded bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                            
                            // 计算压缩后的尺寸
                            int maxWidth = getResources().getDisplayMetrics().widthPixels - 200;
                            int maxHeight = getResources().getDisplayMetrics().heightPixels / 2;
                            
                            float scaleWidth = (float) maxWidth / bitmap.getWidth();
                            float scaleHeight = (float) maxHeight / bitmap.getHeight();
                            float scale = Math.min(scaleWidth, scaleHeight);
                            
                            int newWidth = Math.round(bitmap.getWidth() * scale);
                            int newHeight = Math.round(bitmap.getHeight() * scale);
                            
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                            bitmap.recycle();
                            
                            BitmapDrawable drawable = new BitmapDrawable(getResources(), resizedBitmap);
                            drawable.setBounds(0, 0, newWidth, newHeight);
                            return drawable;
                        }
                    }
                } catch (Exception e) {
                    Log.e("ViewNoteActivity", "Error loading image", e);
                }
                return null;
            };
            
            // 使用自定义的ImageGetter显示HTML内容
            Spanned spanned = Html.fromHtml(currentNote.getContent(), Html.FROM_HTML_MODE_COMPACT, imageGetter, null);
            binding.contentText.setText(spanned);
            binding.contentText.setMovementMethod(LinkMovementMethod.getInstance());
            
            // 设置心情图标
            setMoodIcon(currentNote.getMood());
        }
    }

    private void setMoodIcon(int mood) {
        int moodIconRes;
        switch (mood) {
            case 0:
                moodIconRes = R.drawable.mood_0;
                break;
            case 1:
                moodIconRes = R.drawable.mood_1;
                break;
            case 2:
                moodIconRes = R.drawable.mood_2;
                break;
            case 3:
                moodIconRes = R.drawable.mood_3;
                break;
            case 4:
                moodIconRes = R.drawable.mood_4;
                break;
            case 5:
                moodIconRes = R.drawable.mood_5;
                break;
            default:
                moodIconRes = R.drawable.mood_2;
        }
        binding.moodIcon.setImageResource(moodIconRes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            // 重新加载笔记内容
            loadNote(currentNote.getId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteDao != null) {
            noteDao.close();
        }
        binding = null;
    }
}
