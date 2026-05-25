package com.example.flap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<Level> levelList;
    private OnLevelClickListener listener;

    public interface OnLevelClickListener {
        void onEdit(Level level);
        void onDelete(Level level);
    }

    public LevelAdapter(List<Level> levelList, OnLevelClickListener listener) {
        this.levelList = levelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        Level level = levelList.get(position);
        holder.tvLevelId.setText("ID Level: " + level.getLevelId());
        holder.tvPipeSpeed.setText("Tốc độ ống: " + level.getPipeSpeed());
        holder.tvPipeGap.setText("Khoảng cách ống: " + level.getPipeGap());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(level);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(level);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevelId, tvPipeSpeed, tvPipeGap;
        Button btnEdit, btnDelete;

        LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLevelId = itemView.findViewById(R.id.tv_level_id);
            tvPipeSpeed = itemView.findViewById(R.id.tv_pipe_speed);
            tvPipeGap = itemView.findViewById(R.id.tv_pipe_gap);
            btnEdit = itemView.findViewById(R.id.btnEditLevel);
            btnDelete = itemView.findViewById(R.id.btnDeleteLevel);
        }
    }
}