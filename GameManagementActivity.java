package com.example.flap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameManagementActivity extends AppCompatActivity {

    // Quản lý Điểm số
    private RecyclerView rvScores;
    private ScoreAdapter scoreAdapter;
    private List<ScoreEntry> scoreList;
    private Button btnDeleteAllScores;

    // Quản lý Người dùng
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private Button btnAddNewUser;
    private DatabaseReference usersRef;

    // Quản lý Levels
    private RecyclerView rvLevels;
    private LevelAdapter levelAdapter;
    private List<Level> levelList;
    private Button btnAddNewLevel;
    private DatabaseReference levelsRef;

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_management);

        // Khởi tạo các thành phần quản lý điểm số
        rvScores = findViewById(R.id.rvScores);
        btnDeleteAllScores = findViewById(R.id.btnDeleteAllScores);

        scoreList = new ArrayList<>();
        scoreAdapter = new ScoreAdapter(scoreList, new ScoreAdapter.OnScoreClickListener() {
            @Override
            public void onDelete(ScoreEntry score) {
                deleteScore(score);
            }
        });
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        rvScores.setAdapter(scoreAdapter);
        loadScores();
        btnDeleteAllScores.setOnClickListener(v -> clearAllScores());

        // Khởi tạo các thành phần quản lý người dùng
        rvUsers = findViewById(R.id.rvUsers);
        btnAddNewUser = findViewById(R.id.btnAddNewUser);

        userList = new ArrayList<>();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserClickListener() {
            @Override
            public void onEdit(User user) {
                showEditUserDialog(user);
            }
            @Override
            public void onDelete(User user) {
                deleteUser(user);
            }
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
        loadUsers();
        btnAddNewUser.setOnClickListener(v -> showAddUserDialog());

        // Khởi tạo các thành phần quản lý levels
        rvLevels = findViewById(R.id.rvLevels);
        btnAddNewLevel = findViewById(R.id.btnAddNewLevel);

        levelList = new ArrayList<>();
        levelsRef = FirebaseDatabase.getInstance().getReference("levels");
        levelAdapter = new LevelAdapter(levelList, new LevelAdapter.OnLevelClickListener() {
            @Override
            public void onEdit(Level level) {
                showEditLevelDialog(level);
            }
            @Override
            public void onDelete(Level level) {
                deleteLevel(level);
            }
        });
        rvLevels.setLayoutManager(new LinearLayoutManager(this));
        rvLevels.setAdapter(levelAdapter);
        loadLevels();
        btnAddNewLevel.setOnClickListener(v -> showAddLevelDialog());

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

    // Các phương thức quản lý điểm số
    private void loadScores() {
        SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        String scoresString = prefs.getString("scores", "");

        scoreList.clear();
        if (!scoresString.isEmpty()) {
            String[] entries = scoresString.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    try {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        int level = Integer.parseInt(parts[2]);
                        scoreList.add(new ScoreEntry(name, score, level));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        scoreAdapter.notifyDataSetChanged();
    }

    private void deleteScore(ScoreEntry scoreToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Điểm")
                .setMessage("Bạn có chắc chắn muốn xóa điểm số này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    scoreList.remove(scoreToDelete);
                    saveScores();
                    Toast.makeText(this, "Đã xóa điểm thành công.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveScores() {
        SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry score : scoreList) {
            sb.append(score.getName()).append(":")
                    .append(score.getScore()).append(":")
                    .append(score.getLevel()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("scores", sb.toString());
        editor.apply();
        scoreAdapter.notifyDataSetChanged();
    }

    private void clearAllScores() {
        SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("scores").apply();
        scoreList.clear();
        scoreAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã xóa tất cả điểm.", Toast.LENGTH_SHORT).show();
    }

    // Các phương thức quản lý người dùng
    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.setKey(child.getKey());
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameManagementActivity.this,
                        "Lỗi tải dữ liệu người dùng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText etUsername = dialogView.findViewById(R.id.et_username);
        final EditText etEmail = dialogView.findViewById(R.id.et_email);
        final EditText etPassword = dialogView.findViewById(R.id.et_password);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, email, password);
            usersRef.push().setValue(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(GameManagementActivity.this, "Đã thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText etUsername = dialogView.findViewById(R.id.et_username);
        final EditText etEmail = dialogView.findViewById(R.id.et_email);
        final EditText etPassword = dialogView.findViewById(R.id.et_password);

        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);

            usersRef.child(user.getKey()).setValue(user)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng " + user.getUsername() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    usersRef.child(user.getKey()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa người dùng!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Các phương thức quản lý Levels
    private void loadLevels() {
        levelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                levelList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Level level = child.getValue(Level.class);
                    if (level != null) {
                        // Thêm dòng này để lấy key của level
                        level.setKey(child.getKey());
                        levelList.add(level);
                    }
                }
                levelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameManagementActivity.this, "Lỗi tải levels: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddLevelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_level, null);
        builder.setView(dialogView);

        final EditText etLevelId = dialogView.findViewById(R.id.et_level_id);
        final EditText etPipeSpeed = dialogView.findViewById(R.id.et_pipe_speed);
        final EditText etPipeGap = dialogView.findViewById(R.id.et_pipe_gap);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            try {
                int levelId = Integer.parseInt(etLevelId.getText().toString());
                int pipeSpeed = Integer.parseInt(etPipeSpeed.getText().toString());
                int pipeGap = Integer.parseInt(etPipeGap.getText().toString());

                Level newLevel = new Level(levelId, pipeSpeed, pipeGap);
                levelsRef.push().setValue(newLevel)
                        .addOnSuccessListener(aVoid -> Toast.makeText(GameManagementActivity.this, "Đã thêm level thành công", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm level: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEditLevelDialog(Level level) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_level, null);
        builder.setView(dialogView);

        final EditText etLevelId = dialogView.findViewById(R.id.et_level_id);
        final EditText etPipeSpeed = dialogView.findViewById(R.id.et_pipe_speed);
        final EditText etPipeGap = dialogView.findViewById(R.id.et_pipe_gap);

        etLevelId.setText(String.valueOf(level.getLevelId()));
        etPipeSpeed.setText(String.valueOf(level.getPipeSpeed()));
        etPipeGap.setText(String.valueOf(level.getPipeGap()));

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            try {
                int newLevelId = Integer.parseInt(etLevelId.getText().toString());
                int newPipeSpeed = Integer.parseInt(etPipeSpeed.getText().toString());
                int newPipeGap = Integer.parseInt(etPipeGap.getText().toString());

                // Sử dụng constructor phù hợp
                Level updatedLevel = new Level(newLevelId, newPipeSpeed, newPipeGap);
                levelsRef.child(level.getKey()).setValue(updatedLevel)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật level thành công", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteLevel(Level level) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Level")
                .setMessage("Bạn có chắc chắn muốn xóa level " + level.getLevelId() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Sử dụng getKey() để xóa đúng level
                    levelsRef.child(level.getKey()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa level thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa level: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        Intent intent = new Intent(GameManagementActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}