package com.example.diary.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import android.content.Context;

import com.example.diary.db.NoteDao;
import com.example.diary.model.Note;

import java.util.List;

public class WenXinAPI {
    private static final String API_KEY = "QJiLoARshigjmStJR1R41P33";
    private static final String SECRET_KEY = "jrbnYuZy02CoSgxc76a0k747SkXNT053";
    
    private JSONArray dialogueHistory;
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    private Context context;

    public WenXinAPI(Context context) {
        this.context = context;
        dialogueHistory = new JSONArray();

        // 加载用户的日记历史
        try {
            StringBuilder diaryHistory = new StringBuilder();
            NoteDao noteDao = new NoteDao(context);
            List<Note> notes = noteDao.getAllNotes();
            
            if (!notes.isEmpty()) {
                diaryHistory.append("这是用户之前写的日记内容：\n\n");
                for (Note note : notes) {
                    diaryHistory.append("时间：").append(note.getFormattedTime()).append("\n");
                    diaryHistory.append("标题：").append(note.getTitle()).append("\n");
                    diaryHistory.append("心情：").append(getMoodText(note.getMood())).append("\n");
                    diaryHistory.append("内容：").append(note.getContent()).append("\n\n");
                }
            }
            noteDao.close();
            
            // 将日记历史作为系统消息添加到对话
            if (diaryHistory.length() > 0) {
                JSONObject historyMsg = new JSONObject();
                historyMsg.put("role", "user");
                historyMsg.put("content", diaryHistory.toString());
                dialogueHistory.put(historyMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getMoodText(int mood) {
        switch (mood) {
            case 0: return "非常伤心";
            case 1: return "伤心";
            case 2: return "一般";
            case 3: return "开心";
            case 4: return "非常开心";
            case 5: return "生气";
            default: return "未知";
        }
    }

    public String getAnswer(String userMessage) throws IOException, JSONException {
        // 添加用户消息到对话历史
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        dialogueHistory.put(userMsg);

        // 构建请求体
        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\"messages\":" + dialogueHistory.toString() + 
                           ",\"system\":\"你是一个温暖贴心的日记助手，名叫「心语」。请用温和友善的语气交谈，给予用户情感支持和建议。你可以：1. 倾听用户的心事 2. 给予写日记的建议 3. 帮助用户整理思绪 4. 提供积极的建议。\",\"disable_search\":false,\"enable_citation\":false}";
                           
        RequestBody body = RequestBody.create(mediaType, requestBody);

        // 发送请求
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + getAccessToken())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // 获取响应
        Response response = HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonResponse = new JSONObject(responseBody);
        
        String answer = jsonResponse.getString("result");

        // 添加AI回答到对话历史
        JSONObject aiMsg = new JSONObject();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", answer);
        dialogueHistory.put(aiMsg);

        return answer;
    }

    private String getAccessToken() throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, 
            "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY);
            
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
                
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
} 