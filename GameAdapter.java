package com.example.flap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    public interface OnGameActionListener {
        void onApprove(Game game);
        void onReject(Game game);
    }

    private ArrayList<Game> games;
    private OnGameActionListener listener;

    public GameAdapter(ArrayList<Game> games, OnGameActionListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.tvName.setText(game.getName());
        holder.tvDescription.setText(game.getDescription());
        holder.tvStatus.setText("Trạng thái: " + game.getStatus());

        // Chỉ bật nút nếu game còn pending
        boolean pending = "pending".equals(game.getStatus());
        holder.btnApprove.setEnabled(pending);
        holder.btnReject.setEnabled(pending);

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(game));
        holder.btnReject.setOnClickListener(v -> listener.onReject(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvStatus;
        Button btnApprove, btnReject;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGameName);
            tvDescription = itemView.findViewById(R.id.tvGameDescription);
            tvStatus = itemView.findViewById(R.id.tvGameStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
