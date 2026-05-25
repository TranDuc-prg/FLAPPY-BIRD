package com.example.flap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenuActivity extends AppCompatActivity {

    private ImageButton startButton, leaderboardButton;
    private Button rateButton, gravityButton, multiplayerButton;
    private Button arrowUp1, arrowDown1, arrowUp2, arrowDown2;
    private ImageView birdIcon1, birdIcon2;
    private Button btnManageGames;

    private String currentMode = "single";
    private int indexBird1 = 0, indexBird2 = 0;

    private final int[] birdImages1 = {R.drawable.bird12, R.drawable.bird23, R.drawable.bird33, R.drawable.bird43};
    private final int[] birdImages2 = {R.drawable.bird53, R.drawable.bird63, R.drawable.bird15, R.drawable.bird13};

    public static final String EXTRA_GAME_MODE = "game_mode";
    public static final String EXTRA_SELECTED_BIRD1 = "selected_bird1";
    public static final String EXTRA_SELECTED_BIRD2 = "selected_bird2";

    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "admin@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAuth = FirebaseAuth.getInstance();

        startButton = findViewById(R.id.startButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);
        rateButton = findViewById(R.id.rateButton);
        gravityButton = findViewById(R.id.gravityButton);
        multiplayerButton = findViewById(R.id.multiplayerButton);
        arrowUp1 = findViewById(R.id.arrowUp1);
        arrowDown1 = findViewById(R.id.arrowDown1);
        arrowUp2 = findViewById(R.id.arrowUp2);
        arrowDown2 = findViewById(R.id.arrowDown2);
        birdIcon1 = findViewById(R.id.birdIcon1);
        birdIcon2 = findViewById(R.id.birdIcon2);
        btnManageGames = findViewById(R.id.btnManageGames);

        updateBirdIcons();
        updateGameModeButtons();

        arrowUp1.setOnClickListener(v -> {
            indexBird1 = (indexBird1 + 1) % birdImages1.length;
            updateBirdIcons();
        });
        arrowDown1.setOnClickListener(v -> {
            indexBird1 = (indexBird1 - 1 + birdImages1.length) % birdImages1.length;
            updateBirdIcons();
        });
        arrowUp2.setOnClickListener(v -> {
            indexBird2 = (indexBird2 + 1) % birdImages2.length;
            updateBirdIcons();
        });
        arrowDown2.setOnClickListener(v -> {
            indexBird2 = (indexBird2 - 1 + birdImages2.length) % birdImages2.length;
            updateBirdIcons();
        });

        gravityButton.setOnClickListener(v -> {
            currentMode = "gravity";
            updateGameModeButtons();
        });
        multiplayerButton.setOnClickListener(v -> {
            currentMode = "multiplayer";
            updateGameModeButtons();
        });

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
            intent.putExtra(EXTRA_GAME_MODE, currentMode);
            intent.putExtra(EXTRA_SELECTED_BIRD1, birdImages1[indexBird1]);
            intent.putExtra(EXTRA_SELECTED_BIRD2, birdImages2[indexBird2]);
            startActivity(intent);
        });

        leaderboardButton.setOnClickListener(v -> startActivity(new Intent(MainMenuActivity.this, LeaderboardActivity.class)));

        rateButton.setOnClickListener(v -> {
            String appId = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appId)));
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && isAdminUser(currentUser)) {
            btnManageGames.setVisibility(View.VISIBLE);
        } else {
            btnManageGames.setVisibility(View.GONE);
        }

        btnManageGames.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GameManagementActivity.class);
            startActivity(intent);
        });
    }

    private void updateBirdIcons() {
        birdIcon1.setImageResource(birdImages1[indexBird1]);
        birdIcon2.setImageResource(birdImages2[indexBird2]);
    }

    private void updateGameModeButtons() {
        gravityButton.setEnabled(!currentMode.equals("gravity"));
        multiplayerButton.setEnabled(!currentMode.equals("multiplayer"));
        if (currentMode.equals("single") || currentMode.equals("gravity")) {
            arrowUp2.setVisibility(View.GONE);
            arrowDown2.setVisibility(View.GONE);
            birdIcon2.setVisibility(View.GONE);
        } else {
            arrowUp2.setVisibility(View.VISIBLE);
            arrowDown2.setVisibility(View.VISIBLE);
            birdIcon2.setVisibility(View.VISIBLE);
        }
    }

    private boolean isAdminUser(FirebaseUser user) {
        return user != null && ADMIN_EMAIL.equals(user.getEmail());
    }
}