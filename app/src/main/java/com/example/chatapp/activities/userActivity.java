/**
 * The UserActivity class handles displaying a list of users available for chatting.
 * Users can select a contact to start a chat.
 */
package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.adapters.UsersAdapter;
import com.example.chatapp.databinding.ActivityUserBinding;
import com.example.chatapp.listeners.UserListener;
import com.example.chatapp.modules.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity implements UserListener {

    /**
     * Binding for accessing UI elements in the activity_user layout.
     */
    private ActivityUserBinding binding;

    /**
     * Manager for storing user preferences.
     */
    private PreferenceManager preferenceManager;

    /**
     * Initializes the activity and sets up bindings and listeners.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();  // Set up listeners for UI interactions
        getUser();  // Retrieve the list of users
    }

    /**
     * Sets up click listeners for UI elements.
     */
    private void setListeners() {
        // Navigate back to the previous screen when back button is clicked
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    /**
     * Retrieves a list of users from Firebase Firestore.
     */
    private void getUser() {
        loading(true);  // Show loading indicator while fetching users
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener(task -> {
                    loading(false);  // Hide loading indicator after fetching users
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;  // Skip the current user
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);  // Add user to the list
                        }

                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.userRecyclerView.setAdapter(usersAdapter);  // Set adapter for RecyclerView
                            binding.userRecyclerView.setVisibility(View.VISIBLE);  // Show RecyclerView if users are available
                        } else {
                            showErrorMessage();  // Show error message if no users are available
                        }
                    } else {
                        showErrorMessage();  // Show error message if task is not successful
                    }
                });
    }

    /**
     * Displays an error message when no users are available.
     */
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);  // Make error message visible
    }

    /**
     * Controls visibility of the loading spinner.
     *
     * @param isLoading true if loading, false otherwise
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);  // Show progress bar during loading
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);  // Hide progress bar after loading is complete
        }
    }

    /**
     * Handles the user click event and starts a chat with the selected user.
     *
     * @param user the selected user to chat with
     */
    @Override
    public void onUserClicked(User user) {
        // Initialize a new intent to switch to the ChatActivity class
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);  // Start the ChatActivity
        finish();  // Finish the current activity
    }
}
