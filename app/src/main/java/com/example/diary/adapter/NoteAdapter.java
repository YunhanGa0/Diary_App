package com.example.diary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diary.R;
import com.example.diary.model.Note;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener clickListener;
    private OnNoteLongClickListener longClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNoteClick(note);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onNoteLongClick(note);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView timeTextView;
        private final ImageView moodImageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            moodImageView = itemView.findViewById(R.id.moodImageView);
        }

        public void bind(Note note) {
            titleTextView.setText(note.getTitle());
            contentTextView.setText(note.getContent());
            String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(note.getUpdateTime()));
            timeTextView.setText(formattedTime);
            int moodDrawable = getMoodDrawable(note.getMood());
            if (moodDrawable != 0) {
                moodImageView.setVisibility(View.VISIBLE);
                moodImageView.setImageResource(moodDrawable);
            } else {
                moodImageView.setVisibility(View.GONE);
            }
        }

        private int getMoodDrawable(int mood) {
            switch (mood) {
                case 0:
                    return R.drawable.mood_0;
                case 1:
                    return R.drawable.mood_1;
                case 2:
                    return R.drawable.mood_2;
                case 3:
                    return R.drawable.mood_3;
                case 4:
                    return R.drawable.mood_4;
                case 5:
                    return R.drawable.mood_5;
                default:
                    return 0;
            }
        }
    }
}
