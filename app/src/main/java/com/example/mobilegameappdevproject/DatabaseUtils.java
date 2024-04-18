package com.example.mobilegameappdevproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
public class DatabaseUtils {

    public static void updateLocalHighScore(Context context, String gameMode, int newHighScore) {
        // Updates high score in the local save
        SharedPreferences localHighScore = context.getSharedPreferences(gameMode + "HighScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor localScoreEditor = localHighScore.edit();
        localScoreEditor.putInt("highScore", newHighScore);
        localScoreEditor.apply();
    }

    public static int retrieveLocalHighScore(Context context, String gameMode) {
        // Retrieves high score from the local save
        SharedPreferences localHighScore = context.getSharedPreferences(gameMode + "HighScore", Context.MODE_PRIVATE);
        return localHighScore.getInt("highScore", 0);
    }

    public static void writeUserDataToFirebase(Context context, String username) {
        // Retrieve high scores from local storage
        int noviceHighScore = retrieveLocalHighScore(context, "novice");
        int veteranHighScore = retrieveLocalHighScore(context, "veteran");
        int masterHighScore = retrieveLocalHighScore(context, "master");

        // Initialize Firebase Realtime Database with custom URL
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilegameappdevproject-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference usersRef = database.getReference().child("users");

        // Write user data to Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userNodeRef = usersRef.child(userId); // Reference to the user's node
            userNodeRef.child("username").setValue(username);
            userNodeRef.child("noviceHighscore").setValue(noviceHighScore);
            userNodeRef.child("veteranHighscore").setValue(veteranHighScore);
            userNodeRef.child("masterHighscore").setValue(masterHighScore);
        }
    }

    public static void updateDatabaseHighScore(Context context, String gameMode, int localHighScore, String uid) {
        // Retrieve user's high score from the database
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilegameappdevproject-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("users")
                .child(uid)
                .child(gameMode + "Highscore");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int databaseHighScore = snapshot.getValue(Integer.class);

                    // Compare local score with database score
                    if (localHighScore > databaseHighScore) {
                        // Update database score
                        userRef.setValue(localHighScore);
                    } else {
                        // Update local high score to match database high score
                        updateLocalHighScore(context, gameMode, databaseHighScore);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public static void retrieveUserDetails(){
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilegameappdevproject-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("users")
                .child(MainActivity.currentUser.getUid())
                .child("username");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel.username = dataSnapshot.getValue(String.class);
                    Log.d("MainActivity", "Username Retrieved Successfully.");
                } else {
                    Log.e("MainActivity", "Error retrieving username:");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("MainActivity", "Error retrieving username: " + error.getMessage());
            }
        });
    }
}
