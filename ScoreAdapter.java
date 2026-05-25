package com.example.flap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<ScoreEntry> scoreList;
    private OnScoreClickListener listener;

    public interface OnScoreClickListener {
        void onDelete(ScoreEntry score);
    }

    public ScoreAdapter(List<ScoreEntry> scoreList, OnScoreClickListener listener) {
        this.scoreList = scoreList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreEntry entry = scoreList.get(position);
        holder.tvUsername.setText("Tên: " + entry.getName());
        holder.tvScore.setText("Điểm: " + entry.getScore() + " | Level: " + entry.getLevel());

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                // Sử dụng getAdapterPosition() để đảm bảo lấy đúng vị trí
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onDelete(scoreList.get(currentPosition));
                }
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    // Phương thức giúp cập nhật dữ liệu một cách hiệu quả
    public void setScoreList(List<ScoreEntry> newScoreList) {
        this.scoreList = newScoreList;
        notifyDataSetChanged();
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvScore;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}