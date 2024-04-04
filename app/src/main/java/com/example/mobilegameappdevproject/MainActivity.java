package com.example.mobilegameappdevproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

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
    boolean hasAccount = true, isBgmMuted = false, isDailyScreen = true;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String email, password;


    SoundManager soundManager;
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
                    } else {
                        // User does not exist, handle this case if needed
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
        // Toggle button between daily and all-time rank screen
        soundManager.playSoundEffect(R.raw.sfx_button);
        if(isDailyScreen){
            rankTitle.setImageResource(R.drawable.alltime_title);
            rankTitle.setTag(R.drawable.alltime_title);
        }
        if (!isDailyScreen){
            rankTitle.setImageResource(R.drawable.daily_title);
            rankTitle.setTag(R.drawable.daily_title);
        }
        isDailyScreen = !isDailyScreen;
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
    public void onLoginClick (View v){
        // Logs in user if their credentials are correct
        soundManager.playSoundEffect(R.raw.sfx_button);
        email = String.valueOf(txtEmail.getText());
        password = String.valueOf(txtPassword.getText());

        // Check if email or password is empty, if true then returns void and prompts the user
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please fill up all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loginScreen.setVisibility(View.GONE);
                            rankScreen.setVisibility(View.VISIBLE);
                            currentUser = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login successful.",
                                    Toast.LENGTH_SHORT).show();
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
        email = String.valueOf(txtEmail.getText());
        password = String.valueOf(txtPassword.getText());

        // Check if email or password is empty, if true then prompts the user
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(String.valueOf(txtConfirmPassword.getText()))) {
            Toast.makeText(MainActivity.this, "Please fill up all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If password is equals with confirm password -> Create the user with email and password
        if (password.equals(String.valueOf(txtConfirmPassword.getText()))){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign-up success, update UI with the signed-in user's information
                                currentUser = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Sign up successful.",
                                        Toast.LENGTH_SHORT).show();
                                loginScreen.setVisibility(View.GONE);
                                rankScreen.setVisibility(View.VISIBLE);
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
        // Logs out the user
        Toast.makeText(MainActivity.this, "Logged out.", Toast.LENGTH_SHORT).show();
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        mAuth.signOut();
        currentUser=null;
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
}