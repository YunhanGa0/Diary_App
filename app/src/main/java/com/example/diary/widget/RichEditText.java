package com.example.diary.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class RichEditText extends AppCompatEditText {
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int HEADING_TEXT_SIZE = 20;

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setTextSize(DEFAULT_TEXT_SIZE);
        setTextColor(Color.BLACK);
        setBackgroundColor(Color.TRANSPARENT);
        
        // 允许富文本编辑
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    // 粗体
    public void toggleBold() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        Spannable text = getText();
        StyleSpan[] spans = text.getSpans(start, end, StyleSpan.class);
        
        boolean hasBold = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.BOLD) {
                text.removeSpan(span);
                hasBold = true;
            }
        }
        
        if (!hasBold) {
            text.setSpan(new StyleSpan(Typeface.BOLD), 
                start, end, 
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    // 斜体
    public void toggleItalic() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        Spannable text = getText();
        StyleSpan[] spans = text.getSpans(start, end, StyleSpan.class);
        
        boolean hasItalic = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.ITALIC) {
                text.removeSpan(span);
                hasItalic = true;
            }
        }
        
        if (!hasItalic) {
            text.setSpan(new StyleSpan(Typeface.ITALIC), 
                start, end, 
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    // 下划线
    public void toggleUnderline() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        Spannable text = getText();
        UnderlineSpan[] spans = text.getSpans(start, end, UnderlineSpan.class);
        
        boolean hasUnderline = spans.length > 0;
        if (hasUnderline) {
            for (UnderlineSpan span : spans) {
                text.removeSpan(span);
            }
        } else {
            text.setSpan(new UnderlineSpan(), 
                start, end, 
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    // 设置文本颜色
    public void setTextColor(int color) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        Spannable text = getText();
        text.setSpan(new ForegroundColorSpan(color), 
            start, end, 
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // 设置文本大小
    public void setTextSize(int size) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        Spannable text = getText();
        text.setSpan(new AbsoluteSizeSpan(size, true), 
            start, end, 
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
