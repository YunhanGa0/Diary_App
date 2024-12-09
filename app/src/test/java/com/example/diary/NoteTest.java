package com.example.diary;

import com.example.diary.model.Note;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class NoteTest {
    private Note note;

    @Before
    public void setUp() {
        note = new Note();
    }

    @Test
    public void testNoteInitialization() {
        assertNotNull(note);
        assertTrue(note.getCreateTime() > 0);
        assertEquals(note.getCreateTime(), note.getUpdateTime());
        assertFalse(note.isEncrypted());
    }

    @Test
    public void testSetAndGetTitle() {
        String testTitle = "测试标题";
        note.setTitle(testTitle);
        assertEquals(testTitle, note.getTitle());
    }

    @Test
    public void testSetAndGetContent() {
        String testContent = "这是一个测试内容";
        note.setContent(testContent);
        assertEquals(testContent, note.getContent());
    }

    @Test
    public void testSetAndGetMood() {
        int testMood = 3;
        note.setMood(testMood);
        assertEquals(testMood, note.getMood());
    }

    @Test
    public void testSetAndGetImagePaths() {
        List<String> testPaths = new ArrayList<>();
        testPaths.add("/path/to/image1.jpg");
        testPaths.add("/path/to/image2.jpg");
        note.setImagePaths(testPaths);
        assertEquals(testPaths, note.getImagePaths());
        assertEquals(2, note.getImagePaths().size());
    }

    @Test
    public void testEncryption() {
        assertFalse(note.isEncrypted());
        note.setEncrypted(true);
        assertTrue(note.isEncrypted());
        
        String testPassword = "123456";
        note.setPassword(testPassword);
        assertEquals(testPassword, note.getPassword());
    }

    @Test
    public void testUpdateTime() {
        long initialUpdateTime = note.getUpdateTime();
        try {
            Thread.sleep(100); // 等待100毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long newUpdateTime = System.currentTimeMillis();
        note.setUpdateTime(newUpdateTime);
        assertTrue(note.getUpdateTime() > initialUpdateTime);
    }

    @Test
    public void testFormattedTime() {
        assertNotNull(note.getFormattedTime());
        assertTrue(note.getFormattedTime().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }
} 