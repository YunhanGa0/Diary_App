package com.example.diary;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private RichEditText richEditText;
    private View floatingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        richEditText = findViewById(R.id.richEditText);
        floatingToolbar = getLayoutInflater().inflate(R.layout.floating_toolbar, null);

        ImageButton boldButton = floatingToolbar.findViewById(R.id.boldButton);
        ImageButton italicButton = floatingToolbar.findViewById(R.id.italicButton);
        ImageButton underlineButton = floatingToolbar.findViewById(R.id.underlineButton);

        boldButton.setOnClickListener(v -> richEditText.toggleBold());
        italicButton.setOnClickListener(v -> richEditText.toggleItalic());
        underlineButton.setOnClickListener(v -> richEditText.toggleUnderline());

        richEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                showFloatingToolbar();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                hideFloatingToolbar();
            }
        });
    }

    private void showFloatingToolbar() {
        if (floatingToolbar.getParent() == null) {
            ((ViewGroup) getWindow().getDecorView()).addView(floatingToolbar);
        }
        floatingToolbar.setVisibility(View.VISIBLE);
        positionFloatingToolbar();
    }

    private void hideFloatingToolbar() {
        floatingToolbar.setVisibility(View.GONE);
    }

    private void positionFloatingToolbar() {
        int[] location = new int[2];
        richEditText.getLocationInWindow(location);

        int x = location[0];
        int y = location[1] - floatingToolbar.getHeight() - 20;

        floatingToolbar.setX(x);
        floatingToolbar.setY(Math.max(0, y));
    }
}
