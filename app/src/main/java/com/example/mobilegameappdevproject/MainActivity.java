package com.example.mobilegameappdevproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    public ConstraintLayout menuScreen;
    public ConstraintLayout creditScreen;
    public ConstraintLayout modeScreen;
    public ConstraintLayout loginScreen;
    public ConstraintLayout rankScreen;
    public ConstraintLayout guideScreen;
    public ConstraintLayout mainMenu;
    public ConstraintLayout menuHeader;
    public ConstraintLayout menuFooter;
    public ImageButton btnStart;
    public ImageButton btnCredits;
    public ImageButton btnExit;
    public ImageButton btnRank;
    public ImageButton btnLogin;
    public ImageButton btnSignup;
    public ImageButton btnVolume;
    public ImageView loginTitle;
    public ImageView rankTitle;
    public EditText txtUsername;
    public EditText txtEmail;
    public EditText txtPassword;
    public EditText txtConfirmPassword;
    public TextView txtCreateAccount;
    int rankScreenFlag = 0;
    String highscore;
    boolean hasAccount = true, isBgmMuted = false;
    private FirebaseAuth mAuth;
    static FirebaseUser currentUser;
    SoundManager soundManager;
    ArrayList<LeaderboardModel> LeaderboardModel = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBgmMuted = getIntent().getBooleanExtra("isBgmMuted", false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        soundManager = SoundManager.getInstance(getApplicationContext());
        initializeViews();
        if(!isBgmMuted) {
            soundManager.playBackgroundMusic(R.raw.tavern_loop);
        }
        if(isBgmMuted) {
            btnVolume.setImageResource(R.drawable.icon_muted);
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // User is signed in, check if the user still exists
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
                        // User still exists, set high score and username
                        UserModel.loadUser(MainActivity.this);
                        DatabaseUtils.retrieveUserDetails();
                        // Update all database high score
                        int noviceHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "novice");
                        int veteranHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "veteran");
                        int masterHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "master");
                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "novice", noviceHighScore, currentUser.getUid());
                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "veteran", veteranHighScore, currentUser.getUid());
                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "master", masterHighScore, currentUser.getUid());
                    } else {
                        // User does not exist, handle this case if needed
                        UserModel.onUserLogout(MainActivity.this);
                        currentUser = null;
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.stopBackgroundMusic();
    }
    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }
    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();
    }

    // MENU Methods
    public void btnStart(View v){
        // Start button, this toggles the Choose Mode Screen visibility
        soundManager.playSoundEffect(R.raw.sfx_button);
        modeScreen.setVisibility(View.VISIBLE);
        btnStart.setClickable(false);
        btnExit.setClickable(false);
        btnRank.setClickable(false);
        btnCredits.setClickable(false);
    }


    public void btnCredits(View v){
        // Credits button, this toggles the Credits Screen visibility
        soundManager.playSoundEffect(R.raw.sfx_button);
        menuScreen.setVisibility(View.GONE);
        creditScreen.setVisibility(View.VISIBLE);
    }

    public void btnHome(View v){
        // Home button, this toggles Menu screen visibility
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        returnToMenu();
    }

    private void returnToMenu(){
        // An all-rounder return to menu function if btnHome is not present
        btnStart.setClickable(true);
        btnExit.setClickable(true);
        btnCredits.setClickable(true);
        btnRank.setClickable(true);
        menuScreen.setVisibility(View.VISIBLE);
        mainMenu.setVisibility(View.VISIBLE);
        menuHeader.setVisibility(View.VISIBLE);
        menuFooter.setVisibility(View.VISIBLE);
        loginScreen.setVisibility(View.GONE);
        creditScreen.setVisibility(View.GONE);
        modeScreen.setVisibility(View.GONE);
        rankScreen.setVisibility(View.GONE);
        guideScreen.setVisibility(View.GONE);
    }

    public void btnQuit(View v){
        // Exit button, exits the application
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        soundManager.stopBackgroundMusic();
        finishAffinity();
    }

    public void btnGuide(View v){
        // Guide button, toggles How to Play screen visibility
        soundManager.playSoundEffect(R.raw.sfx_button);
        menuScreen.setVisibility(View.GONE);
        guideScreen.setVisibility(View.VISIBLE);
    }

    public void btnVolume(View v){
        // Volume icon, mutes/un-mutes background music
        soundManager.playSoundEffect(R.raw.sfx_button);
        if (isBgmMuted) {
            btnVolume.setImageResource(R.drawable.icon_volume);
            if(SoundManager.backgroundMusicPlayer == null){
                soundManager.playBackgroundMusic(R.raw.tavern_loop);
                return;
            }
            soundManager.resumeBackgroundMusic();
            isBgmMuted = false;
        } else {
            btnVolume.setImageResource(R.drawable.icon_muted);
            soundManager.pauseBackgroundMusic();
            isBgmMuted = true;
        }
    }

    public void noviceMode(View v){
        // Choose mode Novice button, open game activity Novice mode
        soundManager.playSoundEffect(R.raw.sfx_button);
        soundManager.stopBackgroundMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "novice");
        intent.putExtra("isBgmMuted", isBgmMuted);
        startActivity(intent);
    }
    public void veteranMode(View v){
        // Choose mode Veteran button, open game activity Veteran mode
        soundManager.playSoundEffect(R.raw.sfx_button);
        soundManager.stopBackgroundMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "veteran");
        intent.putExtra("isBgmMuted", isBgmMuted);
        startActivity(intent);
    }
    public void masterMode(View v){
        // Choose mode Master button, open game activity Master mode
        soundManager.playSoundEffect(R.raw.sfx_button);
        soundManager.stopBackgroundMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "master");
        intent.putExtra("isBgmMuted", isBgmMuted);
        startActivity(intent);
    }


    public void btnRank (View v){
        // Leaderboard button, toggles rankScreen if user is logged in, otherwise, toggles loginScreen
        soundManager.playSoundEffect(R.raw.sfx_button);
        mainMenu.setVisibility(View.GONE);
        menuFooter.setVisibility(View.GONE);
        getLeaderboardData();
        LB_RecyclerViewAdapter adapter = new LB_RecyclerViewAdapter(this, LeaderboardModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (currentUser != null) {
            // User is logged in, show the dailyRankScreen
            rankScreen.setVisibility(View.VISIBLE);
        } else {
            // User is not logged in, show the loginScreen
            if(!hasAccount) {
                txtCreateAccount.setText("I already have an account.");
                btnLogin.setVisibility(View.GONE);
                btnSignup.setVisibility(View.VISIBLE);
                txtUsername.setVisibility(View.VISIBLE);
                txtConfirmPassword.setVisibility(View.VISIBLE);
                txtConfirmPassword.setEnabled(true);
            }
            if(hasAccount){
                txtCreateAccount.setText("I don't have an account.");
                btnLogin.setVisibility(View.VISIBLE);
                btnSignup.setVisibility(View.GONE);
                txtUsername.setVisibility(View.GONE);
                txtConfirmPassword.setVisibility(View.GONE);
                txtConfirmPassword.setEnabled(false);
            }
            loginScreen.setVisibility(View.VISIBLE);
        }
    }

    public void rankScreenToggle(View v){
        rankScreenFlag++;
        if (rankScreenFlag == 3){
            rankScreenFlag = 0;
        }
        switch(rankScreenFlag){
            case 0: rankTitle.setImageResource(R.drawable.title_novice);
                break;
            case 1: rankTitle.setImageResource(R.drawable.title_veteran);
                break;
            case 2: rankTitle.setImageResource(R.drawable.title_master);
                break;
        }
        getLeaderboardData();
        LB_RecyclerViewAdapter adapter = new LB_RecyclerViewAdapter(this, LeaderboardModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void initializeViews(){
        // Injects the xml elements to this java activity-controller

        // Screens initialization
        menuScreen = findViewById(R.id.menuScreen);
        creditScreen = findViewById(R.id.creditScreen);
        modeScreen = findViewById(R.id.modeScreen);
        loginScreen = findViewById(R.id.loginScreen);
        guideScreen = findViewById(R.id.guideScreen);
        rankScreen = findViewById(R.id.rankScreen);
        mainMenu = findViewById(R.id.main_menu);
        menuHeader = findViewById(R.id.header_layout);
        menuFooter = findViewById(R.id.footer_layout);
        recyclerView = findViewById(R.id.rankRecyclerView);

        // Buttons initialization;
        btnStart = findViewById(R.id.btnStart);
        btnCredits = findViewById(R.id.btnCredits);
        btnExit = findViewById(R.id.btnExit);
        btnRank = findViewById(R.id.btnRank);
        btnVolume = findViewById(R.id.icon_volume);

        // ImageViews initialization
        loginTitle = findViewById(R.id.loginTitle);
        rankTitle = findViewById(R.id.rankTitle);

        // User Management elements initialization
        txtCreateAccount = findViewById(R.id.txtCreateAcc);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
    }

    // Login Screen Buttons
    public void onLoginClick(View v) {
        // Logs in user if their credentials are correct and compares their current local score from their record in database
        soundManager.playSoundEffect(R.raw.sfx_button);
        UserModel.email = String.valueOf(txtEmail.getText());
        UserModel.password = String.valueOf(txtPassword.getText());

        // Check if email or password is empty, if true then returns void and prompts the user
        if (TextUtils.isEmpty(UserModel.email) || TextUtils.isEmpty(UserModel.password)) {
            Toast.makeText(MainActivity.this, "Please fill up all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(UserModel.email, UserModel.password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login successful.",
                                    Toast.LENGTH_SHORT).show();

                            // Retrieve the username from the database
                            DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilegameappdevproject-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference()
                                    .child("users")
                                    .child(currentUser.getUid());
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Set the username
                                        UserModel.username = snapshot.child("username").getValue(String.class);
                                        // Save the user credentials
                                        UserModel.saveUser(MainActivity.this);
                                        Toast.makeText(MainActivity.this, "Username: " + UserModel.username, Toast.LENGTH_SHORT).show();

                                        // Update all database high score
                                        int noviceHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "novice");
                                        int veteranHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "veteran");
                                        int masterHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "master");
                                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "novice", noviceHighScore, currentUser.getUid());
                                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "veteran", veteranHighScore, currentUser.getUid());
                                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "veteran", veteranHighScore, currentUser.getUid());
                                        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "master", masterHighScore, currentUser.getUid());

                                        // Proceed with UI changes
                                        loginScreen.setVisibility(View.GONE);
                                        rankScreen.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Something wrong with code.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle the error
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Incorrect email or password.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void onSignupClick (View v){
        // Signs up user if credentials are correct
        soundManager.playSoundEffect(R.raw.sfx_button);
        UserModel.email = String.valueOf(txtEmail.getText());
        UserModel.password = String.valueOf(txtPassword.getText());
        UserModel.username = String.valueOf(txtUsername.getText());
        UserModel.confirmPassword = String.valueOf(txtConfirmPassword.getText());

        // Check if email or password is empty, if true then prompts the user
        if (TextUtils.isEmpty(UserModel.email) || TextUtils.isEmpty(UserModel.password) || TextUtils.isEmpty(UserModel.username) || TextUtils.isEmpty(UserModel.confirmPassword)){
            Toast.makeText(MainActivity.this, "Please fill up all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If password is equals with confirm password -> Create the user with email and password
        if (UserModel.password.equals(String.valueOf(txtConfirmPassword.getText()))){
            mAuth.createUserWithEmailAndPassword(UserModel.email, UserModel.password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign-up success, update UI with the signed-in user's information
                                currentUser = mAuth.getCurrentUser();
                                // Reload the user's authentication state
                                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> reloadTask) {
                                        if (reloadTask.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Sign up successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            loginScreen.setVisibility(View.GONE);
                                            rankScreen.setVisibility(View.VISIBLE);
                                            DatabaseUtils.writeUserDataToFirebase(MainActivity.this, UserModel.username);
                                            UserModel.saveUser(MainActivity.this);
                                        } else {
                                            // Reloading failed
                                            Toast.makeText(MainActivity.this, "Authentication failed: " + reloadTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // If sign-up fails, display a message to the user
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    // The email address is already in use by another account
                                    Toast.makeText(MainActivity.this, "This email is already registered.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Some other error occurred during sign-up
                                    Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else{
            Toast.makeText(MainActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLogoutClick(View v){
        // Update all database high score
        int noviceHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "novice");
        int veteranHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "veteran");
        int masterHighScore = DatabaseUtils.retrieveLocalHighScore(MainActivity.this, "master");
        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "novice", noviceHighScore, currentUser.getUid());
        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "veteran", veteranHighScore, currentUser.getUid());
        DatabaseUtils.updateDatabaseHighScore(MainActivity.this, "master", masterHighScore, currentUser.getUid());

        Toast.makeText(MainActivity.this, "Logged out.", Toast.LENGTH_SHORT).show();
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        mAuth.signOut();
        currentUser = null;
        UserModel.onUserLogout(MainActivity.this);
        returnToMenu();
    }

    public void createAccToggle (View v){
        // Navigation between Login screen and Signup screen
        soundManager.playSoundEffect(R.raw.sfx_button);
        hasAccount = !hasAccount;
        if(!hasAccount) {
            loginTitle.setImageResource(R.drawable.signup_title);
            txtCreateAccount.setText("I already have an account.");
            btnLogin.setVisibility(View.GONE);
            btnSignup.setVisibility(View.VISIBLE);
            txtUsername.setVisibility(View.VISIBLE);
            txtConfirmPassword.setVisibility(View.VISIBLE);
            txtConfirmPassword.setEnabled(true);
        }
        if(hasAccount){
            loginTitle.setImageResource(R.drawable.login_title);
            txtCreateAccount.setText("I don't have an account.");
            btnLogin.setVisibility(View.VISIBLE);
            btnSignup.setVisibility(View.GONE);
            txtUsername.setVisibility(View.GONE);
            txtConfirmPassword.setVisibility(View.GONE);
            txtConfirmPassword.setEnabled(false);
        }
    }

    public void getLeaderboardData() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://mobilegameappdevproject-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the LeaderboardModel list before populating it with new data
                LeaderboardModel.clear();

                // Temporary list to store LeaderboardModel objects with scores
                ArrayList<LeaderboardModel> tempList = new ArrayList<>();

                // Iterate through each user node in the database
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the username and scores
                    String username = userSnapshot.child("username").getValue(String.class);

                    if(rankScreenFlag==0) {
                        highscore = String.valueOf(userSnapshot.child("noviceHighscore").getValue(Long.class));
                    } else if(rankScreenFlag==1) {
                        highscore = String.valueOf(userSnapshot.child("veteranHighscore").getValue(Long.class));
                    } else if(rankScreenFlag==2) {
                        highscore = String.valueOf(userSnapshot.child("masterHighscore").getValue(Long.class));
                    }

                    // Create LeaderboardModel objects and add them to temporary list
                    tempList.add(new LeaderboardModel("", username, highscore));

                    Log.d("MainActivity", "Data fetched successfully.");
                }

                // Sort the temporary list in descending order based on score
                Collections.sort(tempList, new Comparator<LeaderboardModel>() {
                    @Override
                    public int compare(LeaderboardModel o1, LeaderboardModel o2) {
                        int score1 = Integer.parseInt(o1.getTxtRankScore());
                        int score2 = Integer.parseInt(o2.getTxtRankScore());
                        Log.d("MainActivity", "Compared successfully.");
                        return Integer.compare(score2, score1);
                    }
                });

                // Assign ranks based on the sorted order
                for (int i = 0; i < tempList.size(); i++) {
                    tempList.get(i).setTxtRank(String.valueOf(i + 1));
                }

                // Add the sorted and ranked list to LeaderboardModel
                LeaderboardModel.addAll(tempList);

                // Notify the RecyclerView adapter about the data changes
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d("MainActivity", "Retrieval failure.");
            }
        });
    }
}