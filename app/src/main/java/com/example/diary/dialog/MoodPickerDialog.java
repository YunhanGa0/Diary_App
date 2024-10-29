package com.example.diary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.example.diary.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MoodPickerDialog extends BottomSheetDialog {
    private OnMoodSelectedListener listener;
    private final int[] moodDrawables = {
        R.drawable.ic_mood,
        R.drawable.mood_1,
        R.drawable.mood_2,
        R.drawable.mood_3,
        R.drawable.mood_4,
        R.drawable.mood_5
    };

    public interface OnMoodSelectedListener {
        void onMoodSelected(int mood);
    }

    public MoodPickerDialog(@NonNull Context context, OnMoodSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mood_picker);

        GridLayout gridLayout = findViewById(R.id.moodGrid);
        
        for (int i = 0; i < moodDrawables.length; i++) {
            ImageView moodView = gridLayout.findViewById(
                    getContext().getResources().getIdentifier(
                            "mood_" + i, "id", getContext().getPackageName()));
            
            final int mood = i;
            moodView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMoodSelected(mood);
                }
                dismiss();
            });
        }
    }
} 