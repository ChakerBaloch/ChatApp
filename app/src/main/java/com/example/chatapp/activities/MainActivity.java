/**
 * MainActivity class for the Chat App.
 * This activity serves as the main screen after the user logs in.
 * It loads user details, allows sign-out, and initializes listeners for UI actions.
 */
package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * Binding object for accessing views in the activity_main.xml layout.
     */
    private ActivityMainBinding binding;

    /**
     * PreferenceManager object to manage shared preferences for user session data.
     */
    private PreferenceManager preferenceManager;

    /**
     * Called when the activity is first created.
     * Initializes binding, loads user details, retrieves FCM token, and sets click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();  // Load user details from shared preferences
        getToken();  // Retrieve FCM token for notifications
        setListener();  // Set event listeners for UI components
    }

    /**
     * Sets click listeners for sign-out and new chat actions.
     */
    private void setListener() {
        // Set listener for sign-out button
        binding.imagesSignOut.setOnClickListener(view -> signOut());
        // Set listener for new chat button
        binding.fabNewChat.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), userActivity.class)));
    }

    /**
     * Loads user details such as name and profile image from shared preferences.
     */
    private void loadUserDetails() {
        // Set user name from shared preferences
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        // Decode profile image from Base64 string and set it to ImageView
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    /**
     * Displays a Toast message.
     *
     * @param message The message to be displayed in the Toast.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Retrieves the FCM token for push notifications.
     */
    private void getToken() {
        // Get FCM token and update it in Firestore
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * Updates the FCM token in Firestore for the logged-in user.
     *
     * @param token The FCM token to be updated.
     */
    private void updateToken(String token) {
        // Get Firestore instance and update user token
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token update successful"))
                .addOnFailureListener(e -> showToast("Unable to update Token"));
    }

    /**
     * Signs out the user, deletes the FCM token, clears user data from preferences, and navigates to the sign-in screen.
     */
    private void signOut() {
        showToast("Signing Out ...");
        // Get Firestore instance and update user token to delete
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    // Clear preferences and navigate to SignInActivity
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("Unable to sign out"));
    }
}
