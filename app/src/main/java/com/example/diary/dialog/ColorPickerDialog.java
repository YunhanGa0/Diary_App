package com.example.diary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import androidx.annotation.NonNull;
import com.example.diary.R;

public class ColorPickerDialog extends Dialog {
    private OnColorSelectedListener listener;
    private final int[] colors = {
        Color.BLACK,
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
        Color.GRAY,
        Color.DKGRAY
    };

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPickerDialog(@NonNull Context context, OnColorSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        setContentView(view);

        GridLayout gridLayout = view.findViewById(R.id.colorGrid);
        
        for (int color : colors) {
            View colorView = new View(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(8, 8, 8, 8);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(color);
            
            colorView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onColorSelected(color);
                }
                dismiss();
            });
            
            gridLayout.addView(colorView);
        }
    }
}
