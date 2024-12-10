package com.example.diary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.adapter.ChatAdapter;
import com.example.diary.model.ChatMessage;
import com.example.diary.utils.WenXinAPI;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private WenXinAPI wenXinAPI;
    private Handler mainHandler;
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupToolbar();
        
        wenXinAPI = new WenXinAPI(this);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        loadingView = findViewById(R.id.loadingView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        // 添加用户消息到聊天列表
        chatAdapter.addMessage(new ChatMessage(message, true));
        messageInput.setEnabled(false);
        sendButton.setEnabled(false);
        showLoading(true);
        
        // 在后台线程发送请求
        new Thread(() -> {
            try {
                String response = wenXinAPI.getAnswer(message);
                mainHandler.post(() -> {
                    // 在主线程更新UI
                    chatAdapter.addMessage(new ChatMessage(response, false));
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    showLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    String errorMsg = "抱歉，出现了问题：" + 
                        (e.getMessage() != null ? e.getMessage() : "未知错误");
                    chatAdapter.addMessage(new ChatMessage(errorMsg, false));
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    showLoading(false);
                });
            }
        }).start();
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        sendButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }
} 