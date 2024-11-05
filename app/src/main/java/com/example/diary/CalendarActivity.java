package com.example.diary;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.diary.adapter.NoteAdapter;
import com.example.diary.databinding.ActivityCalendarBinding;
import com.example.diary.db.NoteDao;
import com.example.diary.model.Note;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity {
    private ActivityCalendarBinding binding;
    private NoteAdapter adapter;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteDao = new NoteDao(this);
        setupViews();
        setupCalendar();
    }

    private void setupViews() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new NoteAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCalendar() {
        Set<CalendarDay> datesWithNotes = noteDao.getDatesWithNotes();
        
        binding.calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return datesWithNotes.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(5, getColor(R.color.primary)));
            }
        });

        binding.calendarView.setOnDateSelectedListener((widget, date, selected) -> {
            List<Note> notes = noteDao.getNotesForDate(date.getDate());
            adapter.setNotes(notes);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 