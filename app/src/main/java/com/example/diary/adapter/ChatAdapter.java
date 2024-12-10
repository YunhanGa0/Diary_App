package com.example.diary.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.R;
import com.example.diary.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final LinearLayout container;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            container = (LinearLayout) itemView;
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            
            // 设置消息的对齐方式
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
            if (message.isUser()) {
                params.gravity = Gravity.END;
                messageText.setBackgroundResource(R.drawable.message_background_user);
                messageText.setTextColor(messageText.getContext().getResources().getColor(R.color.white, null));
            } else {
                params.gravity = Gravity.START;
                messageText.setBackgroundResource(R.drawable.message_background);
                messageText.setTextColor(messageText.getContext().getResources().getColor(android.R.color.black, null));
            }
            messageText.setLayoutParams(params);
        }
    }
} 