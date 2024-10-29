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
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

public class RichEditText extends AppCompatEditText {
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int HEADING_TEXT_SIZE = 20;

    private OnFormatStateChangeListener formatStateChangeListener;

    // 添加接口定义
    public interface OnFormatStateChangeListener {
        void onFormatStateChanged();
    }

    // 定义字体大小常量
    public static final int TEXT_SIZE_SMALL = 14;
    public static final int TEXT_SIZE_NORMAL = 16;
    public static final int TEXT_SIZE_LARGE = 20;
    public static final int TEXT_SIZE_HUGE = 24;

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
        
        // 检查是否有有效的选择区域
        if (start < 0 || end < 0 || start > end || length() == 0) {
            return;
        }
        
        // 如果没有选择文本，使用光标位置
        if (start == end) {
            // 可以选择提示用户，或者不做任何操作
            Toast.makeText(getContext(), "请先选择文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
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
        
        // 检查是否有有效的选择区域
        if (start < 0 || end < 0 || start > end || length() == 0) {
            return;
        }
        
        // 如果没有选择文本，使用光标位置
        if (start == end) {
            Toast.makeText(getContext(), "请先选择文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
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
        
        // 检查是否有有效的选择区域
        if (start < 0 || end < 0 || start > end || length() == 0) {
            return;
        }
        
        // 如果没有选择文本，使用光标位置
        if (start == end) {
            Toast.makeText(getContext(), "请先选择文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
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
        
        // 检查是否有有效的选择区域
        if (start < 0 || end < 0 || start > end || length() == 0) {
            return;
        }
        
        // 如果没有选择文本，使用光标位置
        if (start == end) {
            Toast.makeText(getContext(), "请先选择文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Spannable text = getText();
        text.setSpan(new ForegroundColorSpan(color), 
            start, end, 
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // 设置文本大小
    public void setTextSize(int size) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        // 检查是否有有效的选择区域
        if (start < 0 || end < 0 || start > end || length() == 0) {
            return;
        }
        
        // 如果没有选择文本，使用光标位置
        if (start == end) {
            Toast.makeText(getContext(), "请先选择文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Spannable text = getText();
        
        // 移除现有的字体大小样式
        AbsoluteSizeSpan[] spans = text.getSpans(start, end, AbsoluteSizeSpan.class);
        for (AbsoluteSizeSpan span : spans) {
            text.removeSpan(span);
        }
        
        // 添加新的字体大小样式
        text.setSpan(new AbsoluteSizeSpan(size, true), 
            start, end, 
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
        updateFormatState();
    }

    // 获取当前选中文本的字体大小
    public int getCurrentTextSize() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        
        if (start < 0 || end < 0 || start > end) {
            return TEXT_SIZE_NORMAL;
        }
        
        Spannable text = getText();
        AbsoluteSizeSpan[] spans = text.getSpans(start, end, AbsoluteSizeSpan.class);
        
        if (spans.length > 0) {
            return spans[0].getSize();
        }
        
        return TEXT_SIZE_NORMAL;
    }

    // 设置监听器
    public void setOnFormatStateChangeListener(OnFormatStateChangeListener listener) {
        this.formatStateChangeListener = listener;
    }

    // 更新格式状态
    private void updateFormatState() {
        if (formatStateChangeListener != null) {
            formatStateChangeListener.onFormatStateChanged();
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        updateFormatState();
    }

    // 检查当前选中文本的格式状态
    public boolean isBold() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start < 0 || end < 0 || start > end) {
            return false;
        }

        Spannable text = getText();
        StyleSpan[] spans = text.getSpans(start, end, StyleSpan.class);
        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.BOLD) {
                return true;
            }
        }
        return false;
    }

    public boolean isItalic() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start < 0 || end < 0 || start > end) {
            return false;
        }

        Spannable text = getText();
        StyleSpan[] spans = text.getSpans(start, end, StyleSpan.class);
        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.ITALIC) {
                return true;
            }
        }
        return false;
    }

    public boolean isUnderlined() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start < 0 || end < 0 || start > end) {
            return false;
        }

        Spannable text = getText();
        UnderlineSpan[] spans = text.getSpans(start, end, UnderlineSpan.class);
        return spans.length > 0;
    }
}
