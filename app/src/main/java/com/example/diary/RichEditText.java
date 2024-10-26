package com.example.diary;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class RichEditText extends AppCompatEditText {
    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void toggleBold() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Spannable str = getText();
        StyleSpan[] styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

        boolean hasBold = false;
        for (StyleSpan span : styleSpans) {
            if (span.getStyle() == android.graphics.Typeface.BOLD) {
                str.removeSpan(span);
                hasBold = true;
            }
        }

        if (!hasBold) {
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void toggleItalic() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Spannable str = getText();
        StyleSpan[] styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

        boolean hasItalic = false;
        for (StyleSpan span : styleSpans) {
            if (span.getStyle() == android.graphics.Typeface.ITALIC) {
                str.removeSpan(span);
                hasItalic = true;
            }
        }

        if (!hasItalic) {
            str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void toggleUnderline() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Spannable str = getText();
        UnderlineSpan[] underlineSpans = str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);

        boolean hasUnderline = underlineSpans.length > 0;
        if (hasUnderline) {
            for (UnderlineSpan span : underlineSpans) {
                str.removeSpan(span);
            }
        } else {
            str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return true; // 返回 true 表示我们已经处理了这个事件，系统不需要再显示默认菜单
    }
}
